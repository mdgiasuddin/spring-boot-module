package com.example.module.sqsproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsService {
    private final SqsAsyncClient sqsAsyncClient;

    public void sendMessage(String queueName, String message) {
        sqsAsyncClient.sendMessage(builder -> builder
                .queueUrl(getQueueUrl(queueName))
                .messageBody(message)
        ).join();
    }

    private String getQueueUrl(String queueName) {
        return sqsAsyncClient.getQueueUrl(builder -> builder
                .queueName(queueName)
        ).join().queueUrl();
    }
}
