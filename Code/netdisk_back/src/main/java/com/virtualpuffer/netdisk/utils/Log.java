package com.virtualpuffer.netdisk.utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Log {
    private static final Log logWriter = new Log();
    private FileOutputStream outputStream;
    private FileWriter writer;
    private BufferedWriter writlog;

    private Log() {
        try {
            this.outputStream = new FileOutputStream(Message.getMess("logLocate"));
            this.writer = new FileWriter(Message.getMess("logLocate"),true);
            this.writlog = new BufferedWriter(writer);
        } catch (IOException e) {
        }
    }
    public static Log getLog(){
        return logWriter;
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
    /*日志写入*/
    private void writeLog(String str){
        try {
            writlog.write(str + "\n");
            writlog.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void systemLog(String message){
        getLog().writeLog(getTime()+ " : " + message);
    }

    public void msgLog(String msg){
        systemLog("Message : " + msg);
    }
    public void errorLog(String msg){
        systemLog("Error : " + msg);
    }

}
