package com.virtualpuffer.netdisk.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;

public class FileCollection{
    private LinkedList<String> dir;
    private LinkedList<String> files;
    private int code;
    private String msg;

    public FileCollection() {
    }

    /**
     * @param file 搜索起始路径
     * @param name 目标名字
     * @param path 当前路径
     *
    * */
    public FileCollection(File file, String name,String path,String type){
        this.dir = new LinkedList<String>();
        this.files = new LinkedList<String>();
        if (type == null) {
            search(file,name,path);
        }else {
            search(file,name,path,type);
        }
        if(dir.isEmpty()&&files.isEmpty()){
            msg = "没有找到匹配的文件";
            code = 300;
        }else {
            msg = "已找到匹配的文件";
            code = 200;
        }
    }

    public static FileCollection getInstance(File file,String name,String path,String type)throws FileNotFoundException{
        if(file.exists()){
            return new FileCollection(file,name,path,type);
        }else {
            throw new FileNotFoundException("源路径为空 ： " + file.getAbsolutePath());
        }
    }

    public void search(File file, String name,String path){
        //文件本身
        if(file.getName().matches("(?i).*"+ name +".*")){
            String retPath = file.getPath().substring(path.length());
            dir.add("/" + retPath);
        }
        //子目录分析
        File[] fileArr = file.listFiles();
        for(File get : fileArr){
            //递归
            if(get.isDirectory()){
                search(get,name,path);
            }
            //分析
            if(get.getName().matches("(?i).*"+ name +".*") && get.isFile()){
                String retPath = get.getPath().substring(path.length());
                files.add("/" + retPath);
            }
        }

    }
    public void search(File file, String name,String path,String type){

        //文件本身
        if(file.getName().matches("(?i).*"+ name +".*")){
            String retPath = file.getPath().substring(path.length());
            dir.add("/" + retPath);
        }
        //子目录分析
        File[] fileArr = file.listFiles();
        for(File get : fileArr){
            //递归
            if(get.isDirectory()){
                search(get,name,path,type);
            }
            //分析
            String thisName = get.getName();
            if(thisName.matches("(?i).*"+ name +".*") && get.isFile() && thisName.substring(thisName.lastIndexOf(".") + 1).equals(type)){
                String retPath = get.getPath().substring(path.length());
                files.add("/" + retPath);
            }
        }

    }
    //文件映射，返回dir和file名字，反射的时候，获取的是get的字段
    public int getCode() {
        return code;
    }
    public LinkedList<String> getDir() {
        return dir;
    }

    public LinkedList<String> getFile() {
        return files;
    }

    public String getMsg() {
        return msg;
    }

    public void setDir(LinkedList<String> dir) {
        this.dir = dir;
    }

    public void setFiles(LinkedList<String> files) {
        this.files = files;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
