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

    @Around("execution(* com.flowerable.spring..*(..)) && !execution(* com.flowerable.spring.global..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = null;
        StringBuilder sb = null;

        if(attributes != null) {
            request = attributes.getRequest();
            sb = (StringBuilder) request.getAttribute("sb");
        }

        long start = System.currentTimeMillis();
        try{
            return joinPoint.proceed();
        } finally {
             long finish = System.currentTimeMillis();
             long timeMs  = finish - start;

             if (request != null) {
                 StringBuilder timeSb = (StringBuilder) request.getAttribute("timeSb");
                 if (timeSb == null) {
                     timeSb = new StringBuilder();
                     request.setAttribute("timeSb", timeSb);
                 }

                 String className = joinPoint.getTarget().getClass().getSimpleName();

                 timeSb.append("\nExecute method : ").append(joinPoint.getSignature().getName())
                         .append(", Execute time : ").append(timeMs + "ms\n");

                 if (className.endsWith("Controller")) {
                     String baseInfo = (sb != null) ? sb.toString() : "";
                     String finalLog = baseInfo + timeSb.toString();
                     log.info(finalLog);
                 }
             }
        }
    }
}
