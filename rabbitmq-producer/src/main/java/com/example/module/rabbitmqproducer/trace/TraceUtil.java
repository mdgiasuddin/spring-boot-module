package com.example.module.rabbitmqproducer.trace;

import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TraceUtil {
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    public static String generateTraceId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }

    public static String generateSpanId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }

    public static void setNewTraceId() {
        MDC.put(TRACE_ID, generateTraceId());
        MDC.put(SPAN_ID, generateSpanId());
    }

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }

    public static void setSpanId(String spanId) {
        MDC.put(SPAN_ID, spanId);
    }

    public static void clearTraceId() {
        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);
    }
}
