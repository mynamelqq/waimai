package com.sky.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
//    @Pointcut("execution(* com.sky.service.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
//    public void pointCut(){};
//    @Before("pointCut()")
//    public void autoFillAspect(ProceedingJoinPoint joinPoint) throws Throwable {
//        log.info("around start");
//
//    }
}
