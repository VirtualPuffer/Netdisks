package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.utils.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RestController
public class StaticPage {
    @RequestMapping(value = {"/main/*","/authpage/*","/main","/authpage"})
    public ModelAndView getPage(HttpServletRequest request, HttpServletResponse response){
        return new ModelAndView("/index.html");
    }
/*    @RequestMapping(value = {"/{path}"})
    public Object ret(@PathVariable String path){
        if(path.equals("index.html")){

        }
        return path;
    }*/
}
