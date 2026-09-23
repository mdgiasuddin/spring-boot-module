package com.example.module.kafkaproducer.trace;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.example.module.kafkaproducer.trace.TraceConstants.TRACE_ID;
import static com.example.module.kafkaproducer.trace.TraceConstants.TRACE_ID_HEADER;


public class TraceIdProducerInterceptor implements ProducerInterceptor<Object, Object> {

    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> record) {
        String traceId = MDC.get(TRACE_ID);
        if (traceId != null) {
            record.headers().add(TRACE_ID_HEADER, traceId.getBytes(StandardCharsets.UTF_8));
        }
        return record;
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
