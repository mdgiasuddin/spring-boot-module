### TLS Configuration

##### Generate CA: `generate-ca.sh`

```angular2html
mkdir -p certs && cd certs

openssl genrsa -out ca.key 4096
openssl req -x509 -new -nodes -sha256 -days 3650 \
-key ca.key -out ca.crt \
-subj "/CN=redis-ca"
```

##### Generate Server Cert: `generate-server-cert.sh`

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

##### Generate Client Cert: `generate-client-crt.sh`

```angular2html
cd certs

openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out client_pkcs8.key
openssl req -new -sha256 -key client_pkcs8.key -out client.csr \
-subj "/CN=redis-client"

openssl x509 -req -sha256 -days 365 \
-in client.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
-out client.crt
```

##### Docker Compose File: `docker/docker-compose-redis.yml`

### Steps

* Put all the files in the same directory.
* Run the files sequentially. `generate-ca.sh` -> `generate-server-cert.sh` -> `generate-client-crt.sh`.
* Run `sudo docker compose up -d`.
* Copy `client.crt`, `client_pkcs8.key`, `ca.crt` inside `redis-certs` directory.
* Update permissions of `client_pkcs8.key` to `600`. Command: `chmod 600 ~/redis-cert/client_pkcs8.key`
* Update `application.properties`

```angular2html
server.port=8080

spring.application.name=redis-mtls-demo

# --- SSL Bundle: reads the PEM cert/key files directly, no keystore conversion needed ---
spring.ssl.bundle.pem.redis-mtls.keystore.certificate=file:${REDIS_TLS_CLIENT_CERT:/home/giash.inument_bKash.com/redis-cert/client.crt}
spring.ssl.bundle.pem.redis-mtls.keystore.private-key=file:${REDIS_TLS_CLIENT_KEY:/home/giash.inument_bKash.com/redis-cert/client_pkcs8.key}
spring.ssl.bundle.pem.redis-mtls.truststore.certificate=file:${REDIS_TLS_CA_CERT:/home/giash.inument_bKash.com/redis-cert/ca.crt}

# --- Redis connection: auto-configured by Spring Boot, using the bundle above for mTLS ---
spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6383}
spring.data.redis.ssl.enabled=true
spring.data.redis.ssl.bundle=redis-mtls
spring.data.redis.timeout=5s
```

* Run the application.
