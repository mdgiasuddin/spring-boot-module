package com.example.module.sqsproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.module.sqsproducer.trace.TraceConstants.STRING_DATA_TYPE;
import static com.example.module.sqsproducer.trace.TraceConstants.TRACE_ID;
import static com.example.module.sqsproducer.trace.TraceConstants.TRACE_ID_HEADER;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsService {
    private final SqsAsyncClient sqsAsyncClient;

    private final Map<String, String> queueUrls = new ConcurrentHashMap<>();

    public void sendMessage(String queueName, String message) {
        log.info("Sending message to queue {}, message {}", queueName, message);

        String traceId = MDC.get(TRACE_ID);

        sqsAsyncClient.sendMessage(builder -> {
            builder.queueUrl(getQueueUrl(queueName))
                    .messageBody(message);

            // Propagate trace ID as message attribute header
            if (traceId != null) {
                Map<String, MessageAttributeValue> attributes = new HashMap<>();
                attributes.put(TRACE_ID_HEADER, MessageAttributeValue.builder()
                        .dataType(STRING_DATA_TYPE)
                        .stringValue(traceId)
                        .build());
                builder.messageAttributes(attributes);
            }
        }).join();

        log.info("Message sent to queue {}, message {}", queueName, message);
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
