package com.project.appointmentsystem.Logging;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.project.appointmentsystem.service..*(..)) || " + "execution(* com.project.appointmentsystem.controller..*(..))")
    public Object logMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info(" [START] - {}.{}() | Args: {}", className, methodName, args);

        try {
            Object result = joinPoint.proceed();

            log.info(" [SUCCESS] - {}.{}() |  Result: {}",
                    className, methodName, result);
            return result;
        } catch (Exception e) {
            log.error(" [ERROR] - {}.{}() | Message: {}",
                    className, methodName, e.getMessage());
            throw e;
        }
    }
}