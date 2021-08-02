package com.virtualpuffer.netdisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.virtualpuffer.netdisk.controller","com.virtualpuffer.netdisk.service"})//核心包
public class NetdiskApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetdiskApplication.class, args);
    }

}
