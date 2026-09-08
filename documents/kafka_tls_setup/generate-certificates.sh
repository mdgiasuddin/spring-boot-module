#!/bin/bash
# Generates all mTLS certificates/keystores needed for the Kafka broker
# and the Spring Boot client. Run this once from the project root, then
# go straight to `docker compose up -d` - no other setup step required.
set -e

PASSWORD=changeit
VALIDITY=365
CERT_DIR=certs

echo "==> Cleaning up any previous certs in ./$CERT_DIR"
rm -rf "$CERT_DIR"
mkdir -p "$CERT_DIR"
cd "$CERT_DIR"

echo "==> 1. Creating local CA (LocalKafkaCA)"
openssl req -new -x509 -keyout ca-key -out ca-cert -days $VALIDITY \
  -subj "/CN=LocalKafkaCA" -passout pass:$PASSWORD -nodes

echo "==> 2. Creating broker keystore + signing broker cert"
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

echo "==> 3. Creating broker truststore"
keytool -keystore kafka.broker.truststore.jks -alias CARoot -import \
  -file ca-cert -storepass $PASSWORD -noprompt

echo "==> 4. Creating client keystore + signing client cert (used by the Spring Boot app)"
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

echo "==> 5. Creating client truststore"
keytool -keystore kafka.client.truststore.jks -alias CARoot -import \
  -file ca-cert -storepass $PASSWORD -noprompt

# The broker container reads keystore/truststore passwords from this file
# (referenced by KAFKA_SSL_KEYSTORE_CREDENTIALS / KAFKA_SSL_TRUSTSTORE_CREDENTIALS
# / KAFKA_SSL_KEY_CREDENTIALS in docker-compose.yml)
echo "$PASSWORD" > cert-creds

cd - > /dev/null

echo ""
echo "==> Done. Generated files in ./$CERT_DIR:"
ls -la "$CERT_DIR"
echo ""
echo "You can now run: docker compose up -d"