package com.virtualpuffer.netdisk.service.proxy;

import com.virtualpuffer.netdisk.service.CASService.CasBaseService;
import com.virtualpuffer.netdisk.service.CASService.CasVerifyService;
import com.virtualpuffer.netdisk.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceProxy extends ServiceProxy{
    @Autowired
    CasVerifyService baseService;

    public void sout(){
        System.out.println(baseService);
    }

}
