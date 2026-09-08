### TLS Setup Guideline

##### Generate Certificate

* Run `generate-certificates.sh`
* Copy `spring-client.p12` & `truststore.p12` to `es-certs` directory.

##### Run Docker Compose

* Run `sudo docker compose up -d`

##### Update application.properties

```angular2html
elasticsearch.uris=https://localhost:9200
elasticsearch.username=elastic
elasticsearch.password=ChangeMe123!
elasticsearch.truststore-path=/home/giash.inument_bKash.com/es-certs/truststore.p12
elasticsearch.truststore-password=caTrustPass123
elasticsearch.keystore-path=/home/giash.inument_bKash.com/es-certs/spring-client.p12
elasticsearch.keystore-password=clientKeystorePass123
logging.level.org.springframework.data.elasticsearch.client.WIRE=TRACE
```

##### Run the Application