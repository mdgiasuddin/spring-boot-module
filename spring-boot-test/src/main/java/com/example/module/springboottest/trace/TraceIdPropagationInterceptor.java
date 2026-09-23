package com.example.module.springboottest.trace;

import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static com.example.module.springboottest.trace.TraceConstants.TRACE_ID;
import static com.example.module.springboottest.trace.TraceConstants.TRACE_ID_HEADER;

public class TraceIdPropagationInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public @NonNull ClientHttpResponse intercept(
            @NonNull HttpRequest request, byte @NonNull [] body,
            @NonNull ClientHttpRequestExecution execution
    ) throws IOException {
        String traceId = MDC.get(TRACE_ID);
        if (traceId != null) {
            request.getHeaders().add(TRACE_ID_HEADER, traceId);
        }
        return execution.execute(request, body);
    }
}
