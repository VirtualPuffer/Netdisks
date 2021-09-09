package com.virtualpuffer.netdisk.startup;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ThreadConfig implements AsyncConfigurer {
    /**
    * 线程池配置，服务器是双核，主要是IO，所以调的4线程
    * */
    @Override
    public Executor getAsyncExecutor() {
         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
         executor.setCorePoolSize(3);
         executor.setMaxPoolSize(6);
         executor.setQueueCapacity(6400);
         executor.initialize();
         executor.setAllowCoreThreadTimeOut(true);
         executor.setKeepAliveSeconds(60);
         return executor;
     }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

}
