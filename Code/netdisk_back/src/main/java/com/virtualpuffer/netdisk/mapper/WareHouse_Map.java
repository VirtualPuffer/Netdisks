package com.virtualpuffer.netdisk.mapper;

import java.util.LinkedList;

public interface WareHouse_Map {
    int buildWareHouse(int userID,int capacity,String location);
    int updateSize(int userID,int newSize);
    LinkedList getUserWareHouse(int userID);
}
