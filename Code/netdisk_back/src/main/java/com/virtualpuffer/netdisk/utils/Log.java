package com.virtualpuffer.netdisk.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Log {

    private Log() {
    }

    /*
    * 获取当前时间
    * */
    public static String getTime(){
        Date getDate = new Date();
        SimpleDateFormat sip = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sip.format(getDate);
    }

    public static String getTime(long get){
        Date getDate = new Date(get);
        SimpleDateFormat sip = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sip.format(getDate);
    }

    /*获取调用位置*/
    public static String getLoader(){
        StringBuffer ret = new StringBuffer();
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for(int index = 2;index < stack.length;index++){
            StackTraceElement up = Thread.currentThread().getStackTrace()[index];
            Class get = up.getClass();
            String append = "at " + up.getClassName() +"."+ up.getMethodName() + "(" + up.getFileName() + ":" + up.getLineNumber() + ")" + '\n';
            ret.append(append);
        }
        return ret.toString();
    }

    public String getCaller(){
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        return stack[3].getClassName() + "." + stack[3].getMethodName();
    }

    public void debugLog(String msg){
        Logger log = LogManager.getLogger(getCaller());
        log.debug(msg);
    }

    public void warnLog(String msg){
        Logger log = LogManager.getLogger(getCaller());
        log.warn(msg);
    }

    public void systemLog(String msg){
        Logger log = LogManager.getLogger(getCaller());
        log.info(msg);
    }

    public void errorLog(String msg){
        Logger log = LogManager.getLogger(getCaller());
        log.error(msg);
    }

}
