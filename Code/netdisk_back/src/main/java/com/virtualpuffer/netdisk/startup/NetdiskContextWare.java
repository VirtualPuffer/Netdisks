package com.virtualpuffer.netdisk.startup;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class NetdiskContextWare implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        NetdiskContextWare.applicationContext = applicationContext;
    }
    public static <T> T getBean(Class clazz){
        return (T)applicationContext.getBean(clazz);
    }
    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

}
