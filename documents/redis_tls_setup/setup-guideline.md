### TLS Setup Guideline

##### Generate Certificate

* Run `generate-certificates.sh`
* Copy `ca.crt` & `client.cert` & `client_pkcs8.key` to `redis-certs` directory.

##### Run Docker Compose

* Run `sudo docker compose up -d`

##### Update `application.properties`

```angular2html
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

##### Run the Application