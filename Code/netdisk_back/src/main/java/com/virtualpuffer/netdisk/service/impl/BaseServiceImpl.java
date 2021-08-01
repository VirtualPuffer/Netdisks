package com.virtualpuffer.netdisk.service.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BaseServiceImpl {
    protected static final int BUFFER_SIZE = 4 * 1024;
    protected static String properties = "getMess.properties";

    protected static void close(Closeable cos){
        try {
            if(cos!=null){
                cos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    * 读取配置文件信息
    * */
    protected static String getMess(String source) {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(properties);
        Properties get = new Properties();
        try {

            get.load(in);
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return get.getProperty(source);
    }
    protected  static int getMessByInt(String source){
        return Integer.parseInt(getMess(source));
    }
    protected  static boolean getMessByBoolean(String source){
        return Boolean.getBoolean(getMess(source));
    }
}
