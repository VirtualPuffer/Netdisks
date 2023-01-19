package com.virtualpuffer.netdisk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;

@Controller
@RestController
public class StaticPage {
    public static final String path = "/usr/local/MyTomcat/dist/img";
    @RequestMapping(value = {"/","/main/*","/authpage/*","/main","/authpage","/WSchat","/WSchat/*","/WSchat","/WSchat/*"})
    public Object getPage(HttpServletRequest request, HttpServletResponse response){
        if(JudgeDeviceType.isMobileDevice(request)){
            return "随便返回的网页";
        }else {
            System.out.println("fuck:  "+request.getServletPath());
            return new ModelAndView("/indexe.html");
        }
    }
    @RequestMapping(value = {"/1/{path}"})
    public Object ret(@PathVariable String path){
        return path;
    }
}
