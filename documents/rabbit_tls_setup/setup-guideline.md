### TLS Setup Guideline

##### Generate Certificate

* Run `generate-certificates.sh`
* Copy `client-keystore.p12` & `truststore.p12` to `rabbit-certs` directory.

##### Run Docker Compose

* Run `sudo docker compose up -d`

##### Enable RabbitMQ Plugin

* Run `enable-rabbitmq-plugin.sh`

##### Update `application.properties`

```angular2html
spring.rabbitmq.host=${RABBITMQ_HOST:localhost}
spring.rabbitmq.port=${RABBITMQ_PORT:5671}
spring.rabbitmq.virtual-host=/
# username/password are IGNORED under EXTERNAL auth - harmless to leave,
# but remove them if you want it explicit. Required if using Option A.
spring.rabbitmq.username=${RABBITMQ_USERNAME:admin}
spring.rabbitmq.password=${RABBITMQ_PASSWORD:changeme}

spring.rabbitmq.ssl.enabled=true
spring.rabbitmq.ssl.key-store=file:${RABBITMQ_CLIENT_KEYSTORE:/home/giash.inument_bKash.com/rabbit-certs/client-keystore.p12}
spring.rabbitmq.ssl.key-store-password=${RABBITMQ_KEYSTORE_PASSWORD:changeit}
spring.rabbitmq.ssl.key-store-type=PKCS12
spring.rabbitmq.ssl.trust-store=file:${RABBITMQ_TRUSTSTORE:/home/giash.inument_bKash.com/rabbit-certs/truststore.p12}
spring.rabbitmq.ssl.trust-store-password=${RABBITMQ_TRUSTSTORE_PASSWORD:changeit}
spring.rabbitmq.ssl.trust-store-type=PKCS12
spring.rabbitmq.ssl.verify-hostname=true
spring.rabbitmq.ssl.algorithm=TLSv1.2

spring.rabbitmq.publisher-confirm-type=correlated
spring.rabbitmq.publisher-returns=true
spring.rabbitmq.listener.simple.acknowledge-mode=auto
spring.rabbitmq.listener.simple.retry.enabled=true
spring.rabbitmq.listener.simple.retry.max-attempts=3

app.rabbitmq.exchange=demo.exchange
app.rabbitmq.queue=demo.queue
app.rabbitmq.routing-key=demo.routingkey

logging.level.org.springframework.amqp=INFO
logging.level.com.example.rabbitmqtls=DEBUG
```

##### Run the Application