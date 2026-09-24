package com.example.module.rabbitmqconsumer.trace;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;

import java.util.UUID;

import static com.example.module.rabbitmqconsumer.trace.TraceConstants.SPAN_ID;
import static com.example.module.rabbitmqconsumer.trace.TraceConstants.TRACE_ID;
import static com.example.module.rabbitmqconsumer.trace.TraceConstants.TRACE_ID_HEADER;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class TraceIdConsumerInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Message message = extractMessage(invocation.getArguments());
        String traceId = resolveTraceId(message);
        String spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        MDC.put(TRACE_ID, traceId);
        MDC.put(SPAN_ID, spanId);
        try {
            return invocation.proceed();
        } finally {
            MDC.remove(TRACE_ID);
            MDC.remove(SPAN_ID);
        }
    }

    private Message extractMessage(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Message msg) {
                return msg;
            }
        }
        return null; // listener method uses a converted POJO param, not raw Message
    }

    private String resolveTraceId(Message message) {
        if (message != null) {
            Object header = message.getMessageProperties().getHeaders().get(TRACE_ID_HEADER);
            if (header != null) {
                return header instanceof byte[]
                        ? new String((byte[]) header, UTF_8)
                        : header.toString();
            }
        }
        return UUID.randomUUID().toString().replace("-", ""); // fallback if producer didn't set one
    }
}
