package com.example.module.springsecurityjwt.trace;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.module.springsecurityjwt.trace.TraceConstants.SPAN_ID;

@Aspect
@Component
public class TracingAspect {

    @Around("execution(* com.example.module.springsecurityjwt.service..*(..))")
    public Object trackSpan(ProceedingJoinPoint pjp) throws Throwable {
        String previousSpanId = MDC.get(SPAN_ID); // support nested spans
        String spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        MDC.put(SPAN_ID, spanId);
        try {
            return pjp.proceed();
        } finally {
            if (previousSpanId != null) {
                MDC.put(SPAN_ID, previousSpanId); // restore parent span
            } else {
                MDC.remove(SPAN_ID);
            }
        }
    }
}
