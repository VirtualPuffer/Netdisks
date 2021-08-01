package com.virtualpuffer.netdisk.data;

import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.UserMap;
import org.apache.ibatis.session.SqlSession;

import java.util.LinkedList;

public class UserData{
    String Username;
    String verCode;
    String name;
    boolean vip;
    String photo;

    public UserData(String verCode) {
        SqlSession session = MybatisConnect.getSession();
        LinkedList<User> list = session.getMapper(UserMap.class).getMessage(verCode);
        User res = list.getFirst();
        session.close();
            this.Username = res.getUsername();
            this.name = res.getName();
            this.photo = res.getPhoto();
            this.verCode = verCode;
    }

    public UserData(String username, String verCode, boolean vip, String photo) {
        this.Username = username;
        this.verCode = verCode;
        this.vip = vip;
        this.photo = photo;
    }

    public UserData(String username, String verCode) {
        this.Username = username;
        this.verCode = verCode;
    }

    public String getUsername() {
        return Username;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getVerCode() {
        return verCode;
    }

    public void setVerCode(String verCode) {
        this.verCode = verCode;
    }
}
