package com.virtualpuffer.netdisk.controller;


import com.virtualpuffer.netdisk.controller.base.BaseController;
import com.virtualpuffer.netdisk.entity.File_Map;
import com.virtualpuffer.netdisk.service.impl.FileServiceImpl;
import com.virtualpuffer.netdisk.service.impl.LoginServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api",method = RequestMethod.POST,produces = "application/json")
public class FileController extends BaseController {

    @ResponseBody
    @RequestMapping(value = "/downloadFile")
    public static String get(@RequestBody File_Map on, HttpServletRequest request, HttpServletResponse response){
        LoginServiceImpl loginService = (LoginServiceImpl) request.getAttribute("AuthService");
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/deleteFile")
    public static String delete(@RequestBody File_Map on, HttpServletRequest request, HttpServletResponse response){
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "getDir")
    public static String getDir(@RequestBody File_Map on, HttpServletRequest request, HttpServletResponse response){
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "searchDir")
    public static String searchDir(@RequestBody File_Map on, HttpServletRequest request, HttpServletResponse response){
        return "";
    }
    @ResponseBody
    @RequestMapping(value = "shareFile")
    public static String shareFile(@RequestBody File_Map on, HttpServletRequest request, HttpServletResponse response){
        return "";
    }

}
