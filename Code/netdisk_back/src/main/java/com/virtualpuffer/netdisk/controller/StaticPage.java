package com.virtualpuffer.netdisk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.ArrayList;

@Controller
@RestController
public class StaticPage {
    public static final String path = "/usr/local/MyTomcat/dist/img";
    @RequestMapping(value = {"/main/*","/authpage/*","/main","/authpage","/WSchat","/WSchat/*"})
    public ModelAndView getPage(){
        return new ModelAndView("/index.html");
    }
    @RequestMapping(value = {"/{path}}"})
    public Object ret(@PathVariable String path){
        return path;
    }
}
