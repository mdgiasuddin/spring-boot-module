### TLS Setup Guideline

##### Generate Certificate

* Run `generate-certificates.sh`
* Copy `kafka.client.keystore.jks` & `kafka.client.truststore.jks` to `/src/main/resources/certs` directory.

##### Run Docker Compose

* Run `sudo docker compose up -d`

##### Update application.properties
```angular2html
spring.kafka.security.protocol=SSL
spring.kafka.ssl.trust-store-location=classpath:certs/kafka.client.truststore.jks
spring.kafka.ssl.trust-store-password=${KAFKA_SSL_PASSWORD:changeit}
spring.kafka.ssl.key-store-location=classpath:certs/kafka.client.keystore.jks
spring.kafka.ssl.key-store-password=${KAFKA_SSL_PASSWORD:changeit}
spring.kafka.ssl.key-password=${KAFKA_SSL_PASSWORD:changeit}
```

##### Run the application.