package com.virtualpuffer.netdisk.service.proxy;

import com.virtualpuffer.netdisk.service.CASService.CasBaseService;
import com.virtualpuffer.netdisk.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;

public class LoginServiceProxy extends ServiceProxy{
    @Autowired
    LoginService loginService;
    @Autowired
    CasBaseService baseService;

}
