package com.virtualpuffer.netdisk.service.logService;

import com.alibaba.fastjson.JSON;
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

import javax.json.Json;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class LogAop {
    public static volatile int tag = 0;
    @Pointcut("execution(* com.virtualpuffer.netdisk.Security.securityFilter.APIAuthorizationFilter.*(..))")
    public void logCut(){}

    @Before("logCut()")
    public void beforeAdvice(JoinPoint joinPoint){

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();

        try {
            System.out.println("----------------------  [" + tag++ + "]  ----------------------");
            Signature signature = joinPoint.getSignature();
            System.out.println("操作ip:   " + request.getAttribute("ip"));
            System.out.println("接口路径:  " + request.getServletPath());
            System.out.println("当前时间：  " + Log.getTime());
            System.out.println("参数信息：" + JSON.toJSON(request.getParameterMap()));
            System.out.println(request.getQueryString());
        } catch (Exception exception) {
        }

    }

    @After("logCut()")
    public void afterAdvice(){
    }

}
