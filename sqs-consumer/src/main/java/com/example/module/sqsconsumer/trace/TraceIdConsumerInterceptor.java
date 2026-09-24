package com.example.module.sqsconsumer.trace;

import io.awspring.cloud.sqs.listener.interceptor.MessageInterceptor;
import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.messaging.Message;

import java.util.UUID;

import static com.example.module.sqsconsumer.trace.TraceConstants.SPAN_ID;
import static com.example.module.sqsconsumer.trace.TraceConstants.TRACE_ID;
import static com.example.module.sqsconsumer.trace.TraceConstants.TRACE_ID_HEADER;

public class TraceIdConsumerInterceptor implements MessageInterceptor<Object> {

    @Override
    public @NonNull Message<Object> intercept(Message<Object> message) {
        Object header = message.getHeaders().get(TRACE_ID_HEADER);
        String traceId = (header != null) ? header.toString() : UUID.randomUUID().toString().replace("-", "");
        String spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        MDC.put(TRACE_ID, traceId);
        MDC.put(SPAN_ID, spanId);
        return message;
    }

    @Override
    public void afterProcessing(@NonNull Message<Object> message, Throwable t) {
        // always cleans up, success or failure
        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);
    }
}
