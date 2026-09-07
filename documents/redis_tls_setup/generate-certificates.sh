#!/usr/bin/env bash
#
# setup-redis-tls.sh
#
# Generates the CA, server, and client certificates needed for Redis mTLS,
# then starts Redis via docker compose.
#
# Usage:
#   chmod +x setup-redis-tls.sh
#   ./setup-redis-tls.sh
#
# Run this from the same directory as your docker-compose.yml. It expects
# docker-compose.yml to mount ./certs into the Redis container (e.g. as /tls).

set -euo pipefail

CERT_DIR="certs"
DAYS_CA=3650
DAYS_CERT=365
SAN="subjectAltName=DNS:redis,DNS:localhost,IP:127.0.0.1"

echo "==> Creating ${CERT_DIR}/ ..."
mkdir -p "${CERT_DIR}"
cd "${CERT_DIR}"

# --- 1. Certificate Authority -----------------------------------------------
echo "==> Generating CA key and certificate ..."
openssl genrsa -out ca.key 4096
openssl req -x509 -new -nodes -sha256 -days "${DAYS_CA}" \
  -key ca.key -out ca.crt \
  -subj "/CN=redis-ca"

# --- 2. Server certificate (used by Redis) ----------------------------------
echo "==> Generating server key and certificate (redis.crt) ..."
openssl genrsa -out redis.key 2048
openssl req -new -sha256 -key redis.key -out redis.csr \
  -subj "/CN=redis" \
  -addext "${SAN}"

openssl x509 -req -sha256 -days "${DAYS_CERT}" \
  -in redis.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out redis.crt \
  -extfile <(printf "%s" "${SAN}")

# --- 3. Client certificate (used by the Spring Boot app, PKCS#8 key) -------
echo "==> Generating client key (PKCS#8) and certificate (client.crt) ..."
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out client_pkcs8.key
openssl req -new -sha256 -key client_pkcs8.key -out client.csr \
  -subj "/CN=redis-client"

openssl x509 -req -sha256 -days "${DAYS_CERT}" \
  -in client.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out client.crt

# --- 4. Lock down private key permissions -----------------------------------
echo "==> Setting permissions on private keys ..."
chmod 600 ca.key redis.key client_pkcs8.key

# --- 5. Clean up intermediate files (CSRs, serial file) ---------------------
rm -f redis.csr client.csr ca.srl

cd ..

echo "==> Certificates generated in ./${CERT_DIR}:"
ls -la "${CERT_DIR}"

echo
echo "==> Done. Verify with:"
echo "    redis-cli --tls --cert ${CERT_DIR}/client.crt --key ${CERT_DIR}/client_pkcs8.key --cacert ${CERT_DIR}/ca.crt -h localhost -p 6383 ping"