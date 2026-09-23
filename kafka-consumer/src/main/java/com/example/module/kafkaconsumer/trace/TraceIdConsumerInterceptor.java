package com.example.module.kafkaconsumer.trace;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.kafka.listener.RecordInterceptor;

import java.util.UUID;

import static com.example.module.kafkaconsumer.trace.TraceConstants.SPAN_ID;
import static com.example.module.kafkaconsumer.trace.TraceConstants.TRACE_ID;
import static com.example.module.kafkaconsumer.trace.TraceConstants.TRACE_ID_HEADER;
import static java.nio.charset.StandardCharsets.UTF_8;


public class TraceIdConsumerInterceptor implements RecordInterceptor<Object, Object> {

    @Override
    public ConsumerRecord<Object, Object> intercept(ConsumerRecord<Object, Object> record, @NonNull Consumer<Object, Object> consumer) {
        Header header = record.headers().lastHeader(TRACE_ID_HEADER);
        String traceId = (header != null)
                ? new String(header.value(), UTF_8)
                : UUID.randomUUID().toString().replace("-", ""); // fallback if producer didn't set one

        String spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        MDC.put(TRACE_ID, traceId);
        MDC.put(SPAN_ID, spanId);

        return record;
    }

    @Override
    public void afterRecord(@NonNull ConsumerRecord<Object, Object> record, @NonNull Consumer<Object, Object> consumer) {
        // cleanup after each record, regardless of success/failure
        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);
    }
}
