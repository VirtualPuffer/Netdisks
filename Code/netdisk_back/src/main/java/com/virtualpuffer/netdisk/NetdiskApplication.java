package com.virtualpuffer.netdisk;

import com.virtualpuffer.netdisk.service.proxy.LoginServiceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class},scanBasePackages = {"com.virtualpuffer.netdisk.controller","com.virtualpuffer.netdisk.service","com.virtualpuffer.netdisk.Security"})//核心包
public class NetdiskApplication {

    public static void main(String[] args)throws Exception {
        SpringApplication.run(NetdiskApplication.class, args);
        new LoginServiceProxy().sout();
    }

}
