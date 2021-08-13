package com.virtualpuffer.netdisk.utils;

public class TestTime {
    long start;
    long end;

    public TestTime() {
    }
    public void start(){
        System.out.println("?????????????????????????????????>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        this.start = System.currentTimeMillis();
    }
    public void end(){
        this.end = System.currentTimeMillis();
        System.out.println("__________________>  " + (end - start));
        this.start = System.currentTimeMillis();
    }
}
