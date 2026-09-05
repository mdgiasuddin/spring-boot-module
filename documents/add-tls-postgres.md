### TLS Configuration

##### create-ca.sh

```angular2html
mkdir -p ./certs/ca
cd ./certs/ca

# CA private key
openssl genrsa -out ca.key 4096
chmod 600 ca.key

# Self-signed CA certificate (valid 10 years)
openssl req -new -x509 -days 3650 -key ca.key -out ca.crt \
-subj "/CN=my-postgres-ca"

cd ../..
```

##### re-issue-server-certificate.sh

```angular2html
cd certs

# Create an OpenSSL config with SAN entries
cat > server-san.cnf <
<EOF
        [req]
        distinguished_name=req_distinguished_name
        req_extensions=v3_req
        prompt=no

        [req_distinguished_name]
        CN=localhost

        [v3_req]
        subjectAltName=@alt_names

        [alt_names]
        DNS.1=localhost
        DNS.2=postgres
        IP.1=127.0.0.1
        EOF

        # New key + CSR using that config
        openssl genrsa -out server.key 2048
        openssl req -new -key server.key -out server.csr -config server-san.cnf

        # Sign with your CA, carrying the SAN extension through
        openssl x509 -req -in server.csr -CA ca/ca.crt -CAkey ca/ca.key \
-CAcreateserial -out server.crt -days 365 \
-extensions v3_req -extfile server-san.cnf

rm server.csr
sudo chown 999:999 server.key server.crt   # or via your container-based fix from before
cd ..
```

##### generate-client-certificate.sh

```angular2html
mkdir -p ./certs/client
cd ./certs/client

openssl genrsa -out client.key 2048
chmod 600 client.key

# CN must equal the Postgres username (e.g. "myuser")
openssl req -new -key client.key -out client.csr -subj "/CN=myuser"

openssl x509 -req -in client.csr -CA ../ca/ca.crt -CAkey ../ca/ca.key \
-CAcreateserial -out client.crt -days 365

rm client.csr
cd ../..
```

##### pg_hba.conf

```angular2html
# TYPE    DATABASE  USER  ADDRESS       METHOD    OPTIONS
local     all       all                 trust
hostssl   all       all   0.0.0.0/0     cert      clientcert=verify-full
host      all       all   0.0.0.0/0     reject
```

##### docker-compose.yml

```angular2html
services:
postgres:
image: postgres:16
container_name: postgres_tls
restart: unless-stopped
environment:
POSTGRES_USER: myuser
POSTGRES_PASSWORD: mypassword
POSTGRES_DB: mydb
ports:
- "5437:5432"
volumes:
- pgdata:/var/lib/postgresql/data
- ./certs/server.crt:/var/lib/postgresql/server.crt:ro
- ./certs/server.key:/var/lib/postgresql/server.key:ro
- ./certs/ca/ca.crt:/var/lib/postgresql/ca.crt:ro
- ./pg_hba.conf:/var/lib/postgresql/pg_hba.conf:ro
command: >
-c ssl=on
-c ssl_cert_file=/var/lib/postgresql/server.crt
-c ssl_key_file=/var/lib/postgresql/server.key
-c ssl_ca_file=/var/lib/postgresql/ca.crt
-c hba_file=/var/lib/postgresql/pg_hba.conf

volumes:
pgdata:
```

##### convert-client-key.sh

```angular2html
openssl pkcs8 -topk8 -inform PEM -in certs/client/client.key \
-outform DER -out certs/client/client.pk8 -nocrypt
```

### Steps

* Put all the files in the same directory.
* Run the files in the same order: `create-ca.sh` -> `re-issue-server-certificate.sh` ->
  `generate-client-certificate.sh`
* Run command: `sudo docker compose up -d`
* Run `convert-client-key`
* Copy `ca.crt`, `client.crt`, `client.pk8` to `pg-certs` directory.
* Update the application.properties file with:

```angular2html
spring.datasource.url=jdbc:postgresql://localhost:5437/mydb?ssl=true&sslmode=verify-full&sslcert=/home/giash.inument_bKash.com/pg-certs/client.crt&sslkey=/home/giash.inument_bKash.com/pg-certs/client.pk8&sslrootcert=/home/giash.inument_bKash.com/pg-certs/ca.crt
```

Other properties will be the same.

* Run the application.