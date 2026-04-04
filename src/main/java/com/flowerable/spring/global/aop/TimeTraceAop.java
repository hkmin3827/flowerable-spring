package com.flowerable.spring.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class TimeTraceAop {

    @Around("execution(* com.flowerable.spring.application..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        StringBuilder sb = null;

        if(attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            sb = (StringBuilder) request.getAttribute("sb");
        }

        long start = System.currentTimeMillis();
        try{
            return joinPoint.proceed();
        } finally {
             long finish = System.currentTimeMillis();
             long timeMs  = finish - start;

             if (sb != null) {
                 sb.append("\nExecute method : ").append(joinPoint.getSignature().getName())
                         .append(", Execute time : ").append(timeMs + "ms\n");
             }

             log.info(sb.toString());
        }
    }
}
