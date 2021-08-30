package com.virtualpuffer.netdisk.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class IPRedis {
    public static long refresh = 1000 * 10;
    static LinkedList<Map> list = new LinkedList();
    public static void init(){
        list.add(new HashMap<String,Integer>());
        list.add(new HashMap<String,Integer>());
        list.add(new HashMap<String,Integer>());
    }
    public static void reFresh(){
        list.add(list.getFirst());
        list.removeFirst();
        list.getFirst().clear();
    }
}
