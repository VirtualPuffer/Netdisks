package com.virtualpuffer.netdisk.controller;


import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.controller.base.BaseController;
import com.virtualpuffer.netdisk.entity.File_Map;
import com.virtualpuffer.netdisk.service.impl.FileServiceImpl;
import com.virtualpuffer.netdisk.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

@RestController
@RequestMapping(value = "/api",produces = "application/json")
public class FileController extends BaseController {

    @ResponseBody
    @RequestMapping(value = "/downloadFile",method = RequestMethod.GET)
    public static String get(@RequestBody File_Map on, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/deleteFile")
    public static String delete(@RequestBody File_Map on, HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            FileServiceImpl service = FileServiceImpl.getInstance(on.getFile_Destination(), loginService.getUser().getUSER_ID());
        } catch (FileNotFoundException e) {
            response.sendError(404,"目标不存在");
        }
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "getDir",method = RequestMethod.GET)
    public static Object getDir(String destination, HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            FileServiceImpl service = FileServiceImpl.getInstance(destination, loginService.getUser().getUSER_ID());
            return JSON.toJSON(service.getDirectory());
        } catch (FileNotFoundException e) {
            System.out.println("?");
            response.sendError(404,"文件不存在");
        }  catch (NoSuchFileException e){
            response.sendError(200,"目标不是文件夹");
        }
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
