package com.virtualpuffer.netdisk.mapper;


import com.virtualpuffer.netdisk.entity.User;

import java.sql.Timestamp;
import java.util.LinkedList;

public interface UserMap {
    LinkedList<User> getName(String verCode);
/*    LinkedList contain();*/
    LinkedList verCodeVerify(String verCode);
    LinkedList freeLogVerify(String verCode,String ip);
    LinkedList userLoginVerify(String username,String password);
    LinkedList getMessage(String verCode);

    /*注册逻辑线*/
    LinkedList duplicationUsername(String username);
    int register(String username,String password,String name);
    int updateURL();
    int getIDbyName(String username);

    String getPath(String verCode);
    int userLoginDao(String verCode, long clock, String ip, Timestamp date, String username);
    int locateByVer(String verCode);

    /*获取URL*/
    LinkedList getURL(String );
}
