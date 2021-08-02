package com.virtualpuffer.netdisk.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Message {
    public static String properties = "getMess.properties";
    public static String getMess(String source) {
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
    public  static int getMessByInt(String source){
        return Integer.parseInt(getMess(source));
    }
    public  static boolean getMessByBoolean(String source){
        return Boolean.getBoolean(getMess(source));
    }
}
