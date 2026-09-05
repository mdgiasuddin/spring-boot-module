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

```angular2html
services:
kafka:
image: confluentinc/cp-kafka:8.3.1
container_name: kafka
ports:
- "9093:9093"
environment:
CLUSTER_ID: MkU3OEVBNTcwNTJENDM2Qk   # <-- required by the entrypoint script (KRaft bootstrap)

KAFKA_NODE_ID: 1
KAFKA_PROCESS_ROLES: broker,controller
KAFKA_LISTENERS: SSL://0.0.0.0:9093,CONTROLLER://0.0.0.0:9094
KAFKA_ADVERTISED_LISTENERS: SSL://localhost:9093
KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: SSL:SSL,CONTROLLER:PLAINTEXT
KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9094
KAFKA_INTER_BROKER_LISTENER_NAME: SSL

KAFKA_SSL_KEYSTORE_FILENAME: kafka.broker.keystore.jks
KAFKA_SSL_KEYSTORE_CREDENTIALS: cert-creds
KAFKA_SSL_KEY_CREDENTIALS: cert-creds
KAFKA_SSL_TRUSTSTORE_FILENAME: kafka.broker.truststore.jks
KAFKA_SSL_TRUSTSTORE_CREDENTIALS: cert-creds
KAFKA_SSL_CLIENT_AUTH: required

KAFKA_LOG_DIRS: /var/lib/kafka/data
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
volumes:
- ./certs:/etc/kafka/secrets
- kafka-data:/var/lib/kafka/data

volumes:
kafka-data:
```

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