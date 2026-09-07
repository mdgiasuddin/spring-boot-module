mkdir -p certs
cd certs

# 1. Certificate Authority
openssl genrsa -out ca_key.pem 4096
openssl req -x509 -new -nodes -key ca_key.pem -sha256 -days 3650 \
  -out ca_cert.pem -subj "/CN=RabbitMQ-CA"

# 2. Server key + cert (SAN must match how clients will connect)
openssl genrsa -out server_key.pem 4096
openssl req -new -key server_key.pem -out server.csr -subj "/CN=rabbitmq"
openssl x509 -req -in server.csr -CA ca_cert.pem -CAkey ca_key.pem \
  -CAcreateserial -out server_cert.pem -days 825 -sha256 \
  -extfile <(printf "subjectAltName=DNS:rabbitmq,DNS:localhost,IP:127.0.0.1")

# 3. Client key + cert — the CN here becomes the RabbitMQ username under EXTERNAL auth
openssl genrsa -out client_key.pem 4096
openssl req -new -key client_key.pem -out client.csr -subj "/CN=client"
openssl x509 -req -in client.csr -CA ca_cert.pem -CAkey ca_key.pem \
  -CAcreateserial -out client_cert.pem -days 825 -sha256

chmod 644 *.pem

# Bundles client cert + private key into a PKCS12 keystore
openssl pkcs12 -export -out client-keystore.p12 \
  -inkey client_key.pem -in client_cert.pem -name client -password pass:changeit

# Imports the CA cert into a PKCS12 truststore
keytool -importcert -alias rabbitmq-ca -file ca_cert.pem \
  -keystore truststore.p12 -storetype PKCS12 -storepass changeit -noprompt

cd ../..