package com.virtualpuffer.netdisk;

import com.virtualpuffer.netdisk.controller.UserController;
import com.virtualpuffer.netdisk.data.Mail;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.impl.personal_space.BlogService;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;
import com.virtualpuffer.netdisk.service.messageService.SendMail;
import com.virtualpuffer.netdisk.startup.NetdiskContextWare;
import com.virtualpuffer.onlineChat.chat.Client;
import com.virtualpuffer.onlineChat.chat.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

import static com.virtualpuffer.netdisk.service.messageService.SendMail.sendEmail;

@ServletComponentScan
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class},scanBasePackages = {"com.virtualpuffer.*"})//核心包
public class NetdiskApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(NetdiskApplication.class, args);
    }
}
