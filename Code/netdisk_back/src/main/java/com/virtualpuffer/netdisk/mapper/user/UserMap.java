package com.virtualpuffer.netdisk.mapper.user;


import com.virtualpuffer.netdisk.entity.User;

import java.sql.Timestamp;
import java.util.LinkedList;

public interface UserMap {
    /**
     * 登录逻辑
     * */
    User getUserByPassword(String username);

    User getInstanceByAddr(String addr);

    User userLogin(String username,String password);
    LinkedList<User> getName(String verCode);

    User getUserByID(int userID);

    /*注册逻辑线*/
    User duplicationUsername(String username);
    int register(String username,String password,String name,String address);
    int updateURL();
    int getIDbyUsername(String username);


    //找回密码
    int resetPassword(String password,int userID);


}
