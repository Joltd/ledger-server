package com.evgenltd.ledgerserver.platform.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Aspect
public class LogAspect {

    @Pointcut("within(com.evgenltd.ledgerserver.service.*) || within(com.evgenltd.ledgerserver.controller.*) || within(com.evgenltd.lt.repository.*)")
    public void businessMethodCall() {}

    @Around("businessMethodCall()")
    public Object aroundAtBusinessMethodCall(final ProceedingJoinPoint call) throws Throwable {
        final StopWatch stopWatch = new StopWatch();
        boolean success = true;
        stopWatch.start();
        try {
            return call.proceed();
        } catch (final Throwable t) {
            success = false;
            throw t;
        } finally {
            stopWatch.stop();

            final String args = Arrays.stream(call.getArgs())
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));

            final Logger log = LoggerFactory.getLogger(call.getTarget().getClass());
            log.info(
                    "{}({}); {}ms; {}",
                    call.getSignature().getName(),
                    args,
                    stopWatch.getTotalTimeMillis(),
                    success ? "success" : "failed"
            );

        }
    }
}
