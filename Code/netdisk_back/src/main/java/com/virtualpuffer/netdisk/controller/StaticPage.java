package com.virtualpuffer.netdisk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RestController
public class StaticPage {
    @RequestMapping(value = {"/main/*","/authpage/*","/main","/authpage"})
    public ModelAndView getPage(){
        return new ModelAndView("/index.html");
    }
    @RequestMapping(value = {"/{path}}"})
    public Object ret(@PathVariable String path){
        return path;
    }
}
