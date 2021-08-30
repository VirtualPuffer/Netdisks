package com.virtualpuffer.netdisk.entity;

import java.io.Closeable;
import java.io.IOException;

public class BaseEntity {
    protected static void close(Closeable cos){
        try {
            if(cos!=null){
                cos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
