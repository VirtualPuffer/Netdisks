package com.virtualpuffer.netdisk.utils;

import com.virtualpuffer.netdisk.Singleton;

import java.lang.reflect.Constructor;
import java.util.LinkedList;


/*
* 必须实现Runnable接口
* 不支持继承Thread类
* 单例模式用@Singleton修饰
* */
public class DemoFactory {
private static final String property = Message.getMess("demoThread");
private static final String properDemo="dsa";
private static volatile DemoFactory demoFactory;
private static final LinkedList demoExit = new LinkedList<>();


private DemoFactory(){
}

public static DemoFactory getDemoFactory(){
    if(demoFactory == null){//1
        synchronized (DemoFactory.class){//2
            if(demoFactory == null){//3
                demoFactory = new DemoFactory();//4
            }
        }
    }
    return demoFactory;
}

public void propertiesParse(){
    String get = property;
    String[] a = get.split("/");
    for(String on : a){
        buildDemo(on);
    }
}



public void buildDemo(Class demoClass){
    try {
        Constructor constructor = demoClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object demoInstance = constructor.newInstance();
        if(!(demoInstance instanceof Runnable)){
            return;
        }
        new Thread((Runnable) demoInstance).start();//启动线程
        System.out.println(demoClass.getName() + "  start");
        this.demoExit.add(demoClass);//

    } catch (NoSuchMethodException e) {
        e.printStackTrace();
    } catch (ReflectiveOperationException e) {
        e.printStackTrace();
    }
}
public void buildDemo(String className){
    try {
        if(contain(className)){
            System.out.println(className + " 已经加载");
            return ;
        }
        Class demoClass = Class.forName(className);

        //单例判断
        Singleton singleton = (Singleton) demoClass.getAnnotation(Singleton.class);
        if(singleton==null){
            buildDemo(demoClass);
        }else {
            System.out.println(demoClass + ": single");
            System.out.println(demoClass + "： start");
        }


    } catch (ClassNotFoundException e) {
        System.out.println("ClassNotFound");
    }
}
private boolean contain(String className){
    Class demoClass = getClass(className);
    if(demoExit.contains(demoClass)){
        return true;
    }
    return false;
}
private Class getClass(String className){
    try {
        return Class.forName(className);
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
    return null;
}

}
