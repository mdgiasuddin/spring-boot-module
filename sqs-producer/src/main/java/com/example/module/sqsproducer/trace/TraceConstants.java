package com.example.module.sqsproducer.trace;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TraceConstants {
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String STRING_DATA_TYPE = "String";
}
