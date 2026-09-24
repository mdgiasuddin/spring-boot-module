package com.example.module.rabbitmqproducer.trace;

import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

import static com.example.module.rabbitmqproducer.trace.TraceConstants.TRACE_ID;
import static com.example.module.rabbitmqproducer.trace.TraceConstants.TRACE_ID_HEADER;

public class TraceIdMessagePostProcessor implements MessagePostProcessor {

    @Override
    public @NonNull Message postProcessMessage(@NonNull Message message) {
        String traceId = MDC.get(TRACE_ID);
        if (traceId != null) {
            message.getMessageProperties().setHeader(TRACE_ID_HEADER, traceId);
        }
        return message;
    }
}
