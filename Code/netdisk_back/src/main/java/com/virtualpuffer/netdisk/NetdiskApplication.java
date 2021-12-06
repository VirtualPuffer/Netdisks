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
        System.out.println(System.currentTimeMillis());
        //Class.forName("com.virtualpuffer.netdisk.utils.DemoFactory");
        SpringApplication.run(NetdiskApplication.class, args);
        Thread c = new Thread(new SendMail("zhongyale797@163.com","zhongyale797@163.com","YAWIOJZRTINOIUFG",SendMail.M163_HOST));
        Thread get = new Thread(new SendMail("547798198@qq.com","547798198@qq.com","qykmsmflodptbeea",SendMail.QQ_HOST));
        Thread r = new Thread(new SendMail("1415751897@qq.com","1415751897@qq.com","erthemzwgmbngcbh",SendMail.QQ_HOST));
        c.start();
        get.start();
        r.start();
        Thread a = new Server();
        a.start();
    }
}
