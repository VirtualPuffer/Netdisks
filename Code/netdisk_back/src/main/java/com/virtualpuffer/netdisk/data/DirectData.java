package com.virtualpuffer.netdisk.data;

import java.io.File;
import java.util.LinkedList;

public class DirectData {
    LinkedList<String> dir;
    LinkedList<String> file;
    int code;
    String msg;
    long size;
    public DirectData(File[] get,String msg,int code) {
        this.msg = msg;
        this.code = code;
        this.size = getSize(get,0);
        this.dir = new LinkedList<String>();
        this.file = new LinkedList<String>();
        for(File list : get){
            if(list.isDirectory()){
                dir.add(list.getName());
            }else {
                file.add(list.getName());
            }
        }
    }

    public static long getSize(File[] get){
        return getSize(get,0);
    }
    public static long getSize(File[] get,long size){
        if(get.length == -1){
            return 0;
        }
        for(File list : get){
            //  size += list.length();
            if(list.isDirectory()){
                size += getSize(list.getAbsoluteFile().listFiles(),0);
            }else {
                size += list.length();
            }
        }
        return size;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

    public long getSize() {
        return size;
    }

    public DirectData(String msg,int code) {
        this.code = code;
        this.msg = msg;
    }

    public LinkedList<String> getDir() {
        return dir;
    }

    public LinkedList<String> getFile() {
        return file;
    }
}

class Directory{//文件夹对象
    LinkedList<Directory> dir = new LinkedList<Directory>();
    LinkedList<String> file = new LinkedList<String>();
    String msg;
    int code;

    public Directory(File on){
        File[] fileArr = on.listFiles();
        for(File get : fileArr){
            if(get.isDirectory()){
                this.dir.add(new Directory(get));
            }else{
                this.file.add(get.getName());
            }
        }
    }


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public LinkedList<String> getFile() {
        return file;
    }

    public LinkedList<Directory> getDir() {
        return dir;
    }
}
