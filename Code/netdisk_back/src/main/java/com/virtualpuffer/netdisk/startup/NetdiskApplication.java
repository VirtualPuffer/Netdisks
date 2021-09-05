package com.virtualpuffer.netdisk.startup;

import com.virtualpuffer.netdisk.service.messageService.SendMail;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class},scanBasePackages = {"com.virtualpuffer.netdisk.controller","com.virtualpuffer.netdisk.service","com.virtualpuffer.netdisk.Security","com.virtualpuffer.netdisk.utils"})//核心包
public class NetdiskApplication {
    public static void main(String[] args)throws Exception {
        SpringApplication.run(NetdiskApplication.class, args);
        Thread get = new Thread(new SendMail());
        get.start();
    }
}
