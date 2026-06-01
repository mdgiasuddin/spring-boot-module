package com.example.module.sqsconsumer.listener;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestSqsListener {

    @SqsListener(value = "${aws.sqs.queues.my-queue}")
    public void listen(String event, Acknowledgement acknowledgement) {
        log.info("Event received from SQS, event message {}", event);
        acknowledgement.acknowledge();
    }
}
