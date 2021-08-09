package com.virtualpuffer.netdisk.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.awt.*;
import java.util.Collection;

public class User/* implements UserDetails */{

    private String username;
    private String password;
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

    public String getToken() {
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

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", URL='" + URL + '\'' +
                ", name='" + name + '\'' +
                ", USER_ID=" + USER_ID +
                ", photo='" + photo + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
