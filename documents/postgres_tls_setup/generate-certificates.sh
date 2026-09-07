#!/usr/bin/env bash
# Generates everything needed for Postgres mTLS in one pass:
#   CA -> server cert (with SAN) -> client cert -> PKCS#8 client key -> deploy to ~/pg-certs
#
# Run this once from the directory that will hold docker-compose.yml and pg_hba.conf,
# then `sudo docker compose up -d` immediately after.

set -euo pipefail

# ---- Configuration (edit as needed) ----
CERT_DIR="./certs"
PG_ROLE="myuser"                 # must match POSTGRES_USER / spring.datasource.username
SERVER_CN="localhost"
SAN_DNS=("localhost" "postgres") # add more hostnames here if needed
SAN_IP=("127.0.0.1")
DEPLOY_DIR="${HOME}/pg-certs"    # where the app's application.properties points

# ---- 0. Layout ----
mkdir -p "${CERT_DIR}/ca" "${CERT_DIR}/client"
cd "${CERT_DIR}"

# ---- 1. Certificate Authority ----
echo "==> Generating CA"
openssl genrsa -out ca/ca.key 4096
chmod 600 ca/ca.key
openssl req -new -x509 -days 3650 -key ca/ca.key -out ca/ca.crt \
  -subj "/CN=my-postgres-ca"

# ---- 2. Server certificate (CA-signed, with SAN) ----
echo "==> Generating server certificate"
{
  echo "[req]"
  echo "distinguished_name = req_distinguished_name"
  echo "req_extensions = v3_req"
  echo "prompt = no"
  echo
  echo "[req_distinguished_name]"
  echo "CN = ${SERVER_CN}"
  echo
  echo "[v3_req]"
  echo "subjectAltName = @alt_names"
  echo
  echo "[alt_names]"
  i=1; for dns in "${SAN_DNS[@]}"; do echo "DNS.${i} = ${dns}"; i=$((i+1)); done
  i=1; for ip in "${SAN_IP[@]}"; do echo "IP.${i} = ${ip}"; i=$((i+1)); done
} > server-san.cnf

openssl genrsa -out server.key 2048
openssl req -new -key server.key -out server.csr -config server-san.cnf
openssl x509 -req -in server.csr -CA ca/ca.crt -CAkey ca/ca.key \
  -CAcreateserial -out server.crt -days 365 \
  -extensions v3_req -extfile server-san.cnf
rm server.csr

echo "==> Fixing server key/cert ownership for the postgres container user (uid 999)"
sudo chown 999:999 server.key server.crt

# ---- 3. Client certificate (CN must equal the Postgres role) ----
echo "==> Generating client certificate (CN=${PG_ROLE})"
openssl genrsa -out client/client.key 2048
chmod 600 client/client.key
openssl req -new -key client/client.key -out client/client.csr -subj "/CN=${PG_ROLE}"
openssl x509 -req -in client/client.csr -CA ca/ca.crt -CAkey ca/ca.key \
  -CAcreateserial -out client/client.crt -days 365
rm client/client.csr

# ---- 4. Convert client key to PKCS#8 DER (required by pgjdbc) ----
echo "==> Converting client key to PKCS#8 DER"
openssl pkcs8 -topk8 -inform PEM -in client/client.key \
  -outform DER -out client/client.pk8 -nocrypt

# ---- 5. Deploy client-side files for the Spring Boot app ----
echo "==> Deploying client cert/key/CA to ${DEPLOY_DIR}"
mkdir -p "${DEPLOY_DIR}"
cp client/client.crt client/client.pk8 ca/ca.crt "${DEPLOY_DIR}/"
chmod 600 "${DEPLOY_DIR}/client.pk8"

cd ..

cat <<EOF

Done. Certificates are in ${CERT_DIR}/, client files deployed to ${DEPLOY_DIR}/.

Next steps:
  1. sudo docker compose up -d
  2. Point application.properties at:
     jdbc:postgresql://localhost:5437/mydb?ssl=true&sslmode=verify-full&sslcert=${DEPLOY_DIR}/client.crt&sslkey=${DEPLOY_DIR}/client.pk8&sslrootcert=${DEPLOY_DIR}/ca.crt
EOF