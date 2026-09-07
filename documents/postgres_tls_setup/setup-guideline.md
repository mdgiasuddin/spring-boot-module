### TLS Setup Guideline

##### Generate Certificate

* Run `generate-certificates.sh`
* Copy `ca.crt`, `client.crt` & `client.pk8` to `pg-certs` directory.

##### Run Docker Compose

* Run `sudo docker compose up -d`

##### Update application.properties

```angular2html
spring.datasource.url=jdbc:postgresql://localhost:5437/mydb?ssl=true&sslmode=verify-full&sslcert=/home/giash.inument_bKash.com/pg-certs/client.crt&sslkey=/home/giash.inument_bKash.com/pg-certs/client.pk8&sslrootcert=/home/giash.inument_bKash.com/pg-certs/ca.crt
```

##### Run the Application