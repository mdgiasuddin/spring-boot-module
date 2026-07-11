package com.example.module.recurringsubscription;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RecurringSubscriptionApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecurringSubscriptionApplication.class, args);
    }

}
