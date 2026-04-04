package com.flowerable.spring.global.aop;

import com.flowerable.spring.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogMonitorAop {

    @Pointcut("execution(* com.flowerable.spring.interfaces..*(..)) && execution(* com.flowerable.spring.application..*(..))  && execution(* com.flowerable.spring.domain..*(..))")
    public void all() {}

    @Pointcut("execution(* com.flowerable.spring..*Controller.*(..))")
    public void controllerPointcut() {}

    @Before("controllerPointcut()")
    public void logBeforeController(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null || isExceptionHandlerMethod(joinPoint)) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        StringBuilder sb = new StringBuilder();
        String httpMethod = request.getMethod();
        String methodName = joinPoint.getSignature().getName();
        String params = Arrays.toString(joinPoint.getArgs());

        long now = System.currentTimeMillis();
        request.setAttribute("startTime", now);
        request.setAttribute("sb", sb);

        containUserInfoLogBuilder(joinPoint, sb);
        sb.append("HTTP method: ").append(httpMethod)
                .append(" && Controller method: ").append(methodName)
                .append(" && Parameters: ").append(params).append("\n");
    }

    @AfterThrowing(throwing = "exception", pointcut = "all() && !controllerPointcut()")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        processLog(joinPoint, exception);
    }

    @AfterThrowing(throwing = "exception", pointcut = "controllerPointcut()")
    public void logControllerException(JoinPoint joinPoint, Throwable exception) {
        processLog(joinPoint, exception);
    }

    private void processLog(JoinPoint joinPoint, Throwable exception) {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        StringBuilder sb = (StringBuilder) req.getAttribute("sb");
        String methodName = String.valueOf(joinPoint.getSignature().getName());

        if(sb != null) {
            sb.append("\nEx : ").append(methodName)
                    .append(", Msg : ").append(exception.getMessage());
            log.error(sb.toString(), exception);
        } else {
            log.error("\nEx : {}, Msg : {}", methodName, exception.getMessage(), exception);
        }
    }

    private boolean isExceptionHandlerMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 메서드에 @ExceptionHandler 어노테이션이 있는지 확인
        return method.isAnnotationPresent(org.springframework.web.bind.annotation.ExceptionHandler.class);
    }

    private void containUserInfoLogBuilder(JoinPoint joinPoint, StringBuilder logBuilder) {
        String userEmail = "GUEST@guest.com";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            userEmail = userDetails.getEmail();
        }

        logBuilder.append("[UserEmail: ").append(userEmail).append("] ");
    }
}
