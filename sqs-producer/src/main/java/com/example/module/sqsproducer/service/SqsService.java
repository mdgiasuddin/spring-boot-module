package com.example.module.sqsproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsService {
    private final SqsAsyncClient sqsAsyncClient;

    private final Map<String, String> queueUrls = new ConcurrentHashMap<>();

    public void sendMessage(String queueName, String message) {
        sqsAsyncClient.sendMessage(builder -> builder
                .queueUrl(getQueueUrl(queueName))
                .messageBody(message)
        ).join();
    }

    public String getQueueUrl(String queueName) {
        return queueUrls.computeIfAbsent(
                queueName,
                this::loadQueueUrl
        );
    }

    private String loadQueueUrl(String queueName) {
        log.info("Loading queue url for {}", queueName);
        return sqsAsyncClient.getQueueUrl(r ->
                        r.queueName(queueName))
                .join()
                .queueUrl();
    }
}
