package com.virtualpuffer.netdisk.controller;


import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.controller.base.BaseController;
import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.entity.File_Map;
import com.virtualpuffer.netdisk.service.impl.FileServiceImpl;
import com.virtualpuffer.netdisk.service.impl.UserServiceImpl;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.NoSuchFileException;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/api",produces = "application/json")
public class FileController extends BaseController {

    @ResponseBody
    @RequestMapping(value = "/downloadFile",method = RequestMethod.GET)
    public static String get(@RequestBody File_Map on, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            FileServiceImpl service = FileServiceImpl.getInstance(on.getFile_Destination(), loginService.getUser().getUSER_ID());
            int length = (int)service.downloadFile(response.getOutputStream());
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(service.getFile_name(), "UTF-8"));
            response.setContentLength(length);
        } catch (FileNotFoundException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @ResponseBody
    @RequestMapping(value = "/deleteFile")
    public static String delete(@RequestBody File_Map on, HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            FileServiceImpl service = FileServiceImpl.getInstance(on.getFile_Destination(), loginService.getUser().getUSER_ID());
            service.deleteFileMap();
        } catch (FileNotFoundException e) {
            response.sendError(404,"目标不存在");
        } catch (IOException e){

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
    @RequestMapping(value = "shareFile",method = RequestMethod.GET)
    public static ResponseMessage shareFile(String destination, @Nullable String second, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
        FileServiceImpl service = null;
        try {
            UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
            service = FileServiceImpl.getInstance(destination, loginService.getUser().getUSER_ID());
        } catch (FileNotFoundException e) {
            return ResponseMessage.getSuccessInstance(300,e.getMessage(),null);
        }
        try {
            int time = 900;
            if (second!=null) {
                time = Integer.parseInt(second);
            }
            String url = service.getDownloadURL(time);
            String date = getTime(System.currentTimeMillis() + time * 1000);
            HashMap hashMap = new HashMap();
            hashMap.put("downloadURL",url);//token
            hashMap.put("destination",destination);//名字
            hashMap.put("efficient time",date);
            return ResponseMessage.getSuccessInstance(200,"获取成功",hashMap);
        } catch (Exception e) {
            System.out.println(getTime() + "   ->   未捕获异常: ");
            System.out.println("_______________________________>");
            e.printStackTrace();
            System.out.println("<_______________________________");
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }

}
