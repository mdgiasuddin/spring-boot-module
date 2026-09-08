#!/usr/bin/env bash
#
# generate-certs.sh
#
# One-shot script that:
#   1. Generates a CA using elasticsearch-certutil (silent mode requires
#      this as an explicit step — it can no longer be auto-created inline
#      inside the `cert` command)
#   2. Generates a server cert (es01) + client cert (spring-client) signed
#      by that CA
#   3. Converts the client cert+key into a PKCS#12 keystore
#   4. Builds a PKCS#12 truststore containing the CA
#
# Output layout:
#   certs/
#     ca/ca.crt, ca.key
#     es01/es01.crt, es01.key
#     spring-client/spring-client.crt, spring-client.key, spring-client.p12
#     truststore.p12
#
# Usage:
#   chmod +x generate-certs.sh
#   ./generate-certs.sh
#
# Requires: docker, unzip, openssl, keytool (JDK)

set -euo pipefail

ES_VERSION="8.15.0"
CLIENT_KEYSTORE_PASSWORD="clientKeystorePass123"
TRUSTSTORE_PASSWORD="caTrustPass123"

echo "==> Preparing working directory"
mkdir -p certs
# The elasticsearch image runs its process as UID 1000 inside the container.
# The host-mounted certs/ dir is owned by whoever runs this script, which is
# very likely a different UID, so without this the container can't write
# ca.zip/certs.zip into the mounted volume. Loosen it before generation,
# then tighten it back down at the end.
chmod 777 certs

echo "==> Writing instances.yml"
cat > instances.yml <<'EOF'
instances:
  - name: es01
    dns:
      - es01
      - localhost
    ip:
      - 127.0.0.1
  - name: spring-client
EOF

echo "==> Step 1/4: Generating the CA"
sudo docker run --rm \
  -v "$(pwd)/certs:/certs" \
  "docker.elastic.co/elasticsearch/elasticsearch:${ES_VERSION}" \
  bin/elasticsearch-certutil ca --silent --pem \
  --out /certs/ca.zip --pass ""

(cd certs && sudo unzip -o ca.zip && rm -f ca.zip)
# certutil's --pem output for `ca` nests the files under ca/ca.crt, ca/ca.key

echo "==> Step 2/4: Generating server (es01) and client (spring-client) certs, signed by the CA"
sudo docker run --rm \
  -v "$(pwd)/certs:/certs" \
  -v "$(pwd)/instances.yml:/instances.yml" \
  "docker.elastic.co/elasticsearch/elasticsearch:${ES_VERSION}" \
  bin/elasticsearch-certutil cert --silent --pem \
  --ca-cert /certs/ca/ca.crt --ca-key /certs/ca/ca.key --ca-pass "" \
  --in /instances.yml --out /certs/certs.zip

(cd certs && sudo unzip -o certs.zip && rm -f certs.zip)

# Files just extracted are owned by whatever UID the container ran as
# (typically 1000, the "elasticsearch" user), not the host user running
# this script. Keep permissions wide open through the openssl/keytool
# steps below, which need to WRITE new files into these same
# subdirectories, then lock everything down once at the very end.
sudo chmod -R 777 certs

echo "==> Step 3/4: Building client keystore (spring-client.p12) from client cert + key"
openssl pkcs12 -export \
  -in certs/spring-client/spring-client.crt \
  -inkey certs/spring-client/spring-client.key \
  -certfile certs/ca/ca.crt \
  -out certs/spring-client/spring-client.p12 \
  -name spring-client \
  -password "pass:${CLIENT_KEYSTORE_PASSWORD}"

echo "==> Step 4/4: Building truststore (truststore.p12) from CA cert"
rm -f certs/truststore.p12
keytool -importcert -trustcacerts -noprompt \
  -alias es-ca \
  -file certs/ca/ca.crt \
  -keystore certs/truststore.p12 \
  -storetype PKCS12 \
  -storepass "${TRUSTSTORE_PASSWORD}"

echo "==> Locking down permissions"
sudo chmod -R 755 certs

echo "==> Done. Resulting files:"
find certs -type f | sort

echo ""
echo "Client keystore password:  ${CLIENT_KEYSTORE_PASSWORD}"
echo "Truststore password:       ${TRUSTSTORE_PASSWORD}"
echo "(Update application.yml / docker-compose.yml if you change these.)"