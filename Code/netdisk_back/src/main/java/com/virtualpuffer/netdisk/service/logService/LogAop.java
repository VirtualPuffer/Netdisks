package com.virtualpuffer.netdisk.service.logService;

import com.virtualpuffer.netdisk.utils.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class LogAop {
    public static volatile int tag = 0;
    @Pointcut("execution(* com.virtualpuffer.netdisk.controller.*.*(..))")
    public void logCut(){}

    @Before("logCut()")
    public void beforeAdvice(JoinPoint joinPoint){

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();

        System.out.println("----------------------  [" + tag++ + "]  ----------------------");
        Signature signature = joinPoint.getSignature();
        System.out.println("当前时间：  " + Log.getTime());
        System.out.println("返回目标方法的签名：" + signature);
        System.out.println("代理方法：" + signature.getName());
        Object[] args = joinPoint.getArgs();
        System.out.println("参数信息：" + Arrays.asList(args));

    }

    @After("logCut()")
    public void afterAdvice(){
    }

}
