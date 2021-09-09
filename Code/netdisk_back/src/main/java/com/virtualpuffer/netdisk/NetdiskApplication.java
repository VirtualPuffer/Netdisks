package com.virtualpuffer.netdisk;

import com.virtualpuffer.netdisk.controller.UserController;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;
import com.virtualpuffer.netdisk.service.messageService.SendMail;
import com.virtualpuffer.netdisk.startup.NetdiskContextWare;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class},scanBasePackages = {"com.virtualpuffer.netdisk.*"})//核心包
public class NetdiskApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(NetdiskApplication.class, args);
        Thread get = new Thread(new SendMail("547798198@qq.com","547798198@qq.com","qykmsmflodptbeea","smtp.qq.com"));
        get.start();
    }
}
