package com.virtualpuffer.netdisk.entity;

public class User {
    private String Password;
    private String username;
    private String URL;
    private String name;
    private int USER_ID;
    private String photo;

    public User() {
    }

    public User(String password, String username, String URL, String name, int USER_ID, String photo) {
        Password = password;
        this.username = username;
        this.URL = URL;
        this.name = name;
        this.USER_ID = USER_ID;
        this.photo = photo;
    }

    public void setPassword(String password) {
        Password = password;
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

    public String getPassword() {
        return Password;
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
}
