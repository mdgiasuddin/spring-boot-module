### TLS Configuration

##### generate-ca.sh

```angular2html
mkdir -p certs && cd certs

openssl genrsa -out ca.key 4096
openssl req -x509 -new -nodes -sha256 -days 3650 \
-key ca.key -out ca.crt \
-subj "/CN=redis-ca"
```

##### generate-server-cert.sh

```angular2html
cd certs

openssl genrsa -out redis.key 2048
openssl req -new -sha256 -key redis.key -out redis.csr \
-subj "/CN=redis" \
-addext "subjectAltName=DNS:redis,DNS:localhost,IP:127.0.0.1"

openssl x509 -req -sha256 -days 365 \
-in redis.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
-out redis.crt \
-extfile <(printf "subjectAltName=DNS:redis,DNS:localhost,IP:127.0.0.1")
```

##### generate-client-crt.sh

```angular2html
cd certs

openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out client_pkcs8.key
openssl req -new -sha256 -key client_pkcs8.key -out client.csr \
-subj "/CN=redis-client"

openssl x509 -req -sha256 -days 365 \
-in client.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
-out client.crt
```

##### docker-compose.yml

```angular2html
services:
redis:
image: redis/redis-stack-server:latest
container_name: redis-stack-tls
environment:
- REDIS_ARGS=--tls-port 6379 --port 0 --tls-cert-file /tls/redis.crt --tls-key-file /tls/redis.key --tls-ca-cert-file /tls/ca.crt --tls-auth-clients yes
volumes:
- ./certs:/tls:ro
- redis-data:/data
ports:
- "6383:6379"
restart: unless-stopped

volumes:
redis-data:
```

### Steps

* Put all the files in the same directory.
* Run the files sequentially. `generate-ca.sh` -> `generate-server-cert.sh` -> `generate-client-crt.sh`.
* Run `sudo docker compose up -d`.
* Copy `client.crt`, `client_pkcs8.key`, `ca.crt` inside `redis-certs` directory.
* Update permissions of `client_pkcs8.key` to `600`. Command: `chmod 600 ~/redis-cert/client_pkcs8.key`
* Update `application.yml`

```angular2html
spring:
ssl:
bundle:
pem:
redis-mtls:
keystore:
certificate: file:${REDIS_TLS_CLIENT_CERT:/home/giash.inument_bKash.com/redis-cert/client.crt}
private-key: file:${REDIS_TLS_CLIENT_KEY:/home/giash.inument_bKash.com/redis-cert/client_pkcs8.key}
truststore:
certificate: file:${REDIS_TLS_CA_CERT:/home/giash.inument_bKash.com/redis-cert/ca.crt}

data:
redis:
host: ${REDIS_HOST:localhost}
port: ${REDIS_PORT:6383}
ssl:
enabled: true
bundle: redis-mtls
timeout: 5s
```

* Run the application.
