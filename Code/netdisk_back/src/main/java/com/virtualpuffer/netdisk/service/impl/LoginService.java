package com.virtualpuffer.netdisk.service.impl;

import com.virtualpuffer.netdisk.entity.User;

public class LoginService {
    /**
    * 初始阶段只传入账号密码，登录状态
     * 登录成功后完善内容
    *
    * */
    private User  user;
    public LoginService(User loginUser){
        String username = loginUser.getUsername();
        String password = loginUser.getPassword();

    }
}
