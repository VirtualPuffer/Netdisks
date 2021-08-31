package com.virtualpuffer.netdisk.entity;

import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.awt.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class User extends BaseEntity implements Serializable {

    private String mailBox;
    private String username;
    private String password;
    private String address;
    private String URL;
    private String name;
    private int USER_ID;
    private String photo;
    private String token;
    private String ip;

    public User() { }

    public User(String username, String password,int id) {
        this.username = username;
        this.password = password;
        this.USER_ID = id;
    }

    public String getToken(String tokenTag) {
        if (this.token == null) {
            Map<String,Object> map = new HashMap();
            map.put("tokenTag",tokenTag);
            map.put("username",this.username);
            map.put("password",this.password);
            map.put("userID",this.USER_ID);
            map.put("ip",this.ip);
            this.token = BaseServiceImpl.createToken(60*60*24,map,this.username,null);
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public String getURL() {
        return URL;
    }

    public String getName() {
        return name;
    }

    public int getUSER_ID() {
        return USER_ID;
    }

    public String getPhoto() {
        return photo;
    }

    public String getMailBox() {
        return mailBox;
    }

    public void setMailBox(String mailBox) {
        this.mailBox = mailBox;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    @Override
    public String toString() {
        return "User{" +
                "mailBox='" + mailBox + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", URL='" + URL + '\'' +
                ", name='" + name + '\'' +
                ", USER_ID=" + USER_ID +
                ", photo='" + photo + '\'' +
                ", token='" + token + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
