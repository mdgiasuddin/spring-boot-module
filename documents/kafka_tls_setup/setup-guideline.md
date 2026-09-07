#### TLS Configuration

##### Generate Certificate => `generate-certs.sh`

```angular2html
#!/bin/bash
PASSWORD=changeit
VALIDITY=365

mkdir -p certs && cd certs

# 1. Create CA
openssl req -new -x509 -keyout ca-key -out ca-cert -days $VALIDITY \
-subj "/CN=LocalKafkaCA" -passout pass:$PASSWORD -nodes

# 2. Broker keystore
keytool -genkey -keystore kafka.broker.keystore.jks -alias broker \
-validity $VALIDITY -keyalg RSA -storepass $PASSWORD -keypass $PASSWORD \
-dname "CN=kafka" -ext SAN=dns:kafka,dns:localhost

keytool -keystore kafka.broker.keystore.jks -alias broker -certreq \
-file broker.csr -storepass $PASSWORD

openssl x509 -req -CA ca-cert -CAkey ca-key -in broker.csr \
-out broker-signed.crt -days $VALIDITY -CAcreateserial \
-extfile <(echo "subjectAltName=DNS:kafka,DNS:localhost")

keytool -keystore kafka.broker.keystore.jks -alias CARoot -import \
-file ca-cert -storepass $PASSWORD -noprompt
keytool -keystore kafka.broker.keystore.jks -alias broker -import \
-file broker-signed.crt -storepass $PASSWORD -noprompt

# 3. Broker truststore
keytool -keystore kafka.broker.truststore.jks -alias CARoot -import \
-file ca-cert -storepass $PASSWORD -noprompt

# 4. Client keystore (for producer/consumer)
keytool -genkey -keystore kafka.client.keystore.jks -alias client \
-validity $VALIDITY -keyalg RSA -storepass $PASSWORD -keypass $PASSWORD \
-dname "CN=client"

keytool -keystore kafka.client.keystore.jks -alias client -certreq \
-file client.csr -storepass $PASSWORD

openssl x509 -req -CA ca-cert -CAkey ca-key -in client.csr \
-out client-signed.crt -days $VALIDITY -CAcreateserial

keytool -keystore kafka.client.keystore.jks -alias CARoot -import \
-file ca-cert -storepass $PASSWORD -noprompt
keytool -keystore kafka.client.keystore.jks -alias client -import \
-file client-signed.crt -storepass $PASSWORD -noprompt

# 5. Client truststore
keytool -keystore kafka.client.truststore.jks -alias CARoot -import \
-file ca-cert -storepass $PASSWORD -noprompt

cd ..
echo "Certs generated in ./certs"
```

##### Docker Compose File => `docker-compose.yml`

##### Steps to Follow

* Keep `generate-certs.sh` and `docker-compose.yml` in the same directory.
* Generate Certificate and Run Docker Compose. `Run Command`

```angular2html
chmod +x generate-certs.sh
./generate-certs.sh
echo "changeit" > certs/cert-creds
docker compose up -d
```

* Copy `kafka.client.truststore.jks` and `kafka.client.keystore.jks` to `resource/certs`.
* Add the following properties in the `application.properties`.

```angular2html
spring.kafka.security.protocol=SSL
spring.kafka.ssl.trust-store-location=classpath:certs/kafka.client.truststore.jks
spring.kafka.ssl.trust-store-password=${KAFKA_SSL_PASSWORD:changeit}
spring.kafka.ssl.key-store-location=classpath:certs/kafka.client.keystore.jks
spring.kafka.ssl.key-store-password=${KAFKA_SSL_PASSWORD:changeit}
spring.kafka.ssl.key-password=${KAFKA_SSL_PASSWORD:changeit}
```

* Run the application.