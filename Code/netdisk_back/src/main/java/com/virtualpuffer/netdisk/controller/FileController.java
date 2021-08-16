package com.virtualpuffer.netdisk.controller;


import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.controller.base.BaseController;
import com.virtualpuffer.netdisk.data.FileCollection;
import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.entity.File_Map;
import com.virtualpuffer.netdisk.service.impl.FileServiceImpl;
import com.virtualpuffer.netdisk.service.impl.UserServiceImpl;
import com.virtualpuffer.netdisk.utils.RandomString;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.NoSuchFileException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class FileController extends BaseController {
    public FileController() {
    }

    @ResponseBody
    @RequestMapping(value = "/downloadFile",method = RequestMethod.GET)
    public ResponseMessage get(String destination, HttpServletRequest request, HttpServletResponse response){

        System.out.println(destination + "des ______________________________________>>>>>>>>>>>>>>>");

        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            FileServiceImpl service = FileServiceImpl.getInstance(destination, loginService.getUser().getUSER_ID());
            int length = (int)service.downloadFile(response.getOutputStream());
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(service.getFile_name(), "UTF-8"));
            response.setContentLength(length);
            return ResponseMessage.getSuccessInstance(200,"传输成功",null);
        } catch (FileNotFoundException e) {
            return ResponseMessage.getExceptionInstance(404,"文件未找到",null);
        }  catch (RuntimeException e){
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    public ResponseMessage upload(String filePath,MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        if(filePath == null || filePath.equals("")){
            filePath = "";
        }
        if(file == null){
            return ResponseMessage.getExceptionInstance(404,"未找到上传的文件流",null);
        }
        try {
            String path = filePath + "/" + file.getOriginalFilename();
            FileServiceImpl service = FileServiceImpl.getInstance(path, loginService.getUser().getUSER_ID());

            System.out.println(file.getInputStream().available());

            service.uploadFile(file.getInputStream());
            return ResponseMessage.getSuccessInstance(200,"文件上传成功",null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(404,"传输地址无效",null);
        }  catch (RuntimeException e){
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/mkdir",method = RequestMethod.POST,produces = "application/json")
    public ResponseMessage mkdir(@RequestBody File_Map on, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            FileServiceImpl service = FileServiceImpl.getInstance(on.getDestination(),loginService.getUser().getUSER_ID());
            service.mkdir();
            return ResponseMessage.getSuccessInstance(200,"文件夹创建成功",null);
        } catch (FileNotFoundException e) {
            return ResponseMessage.getExceptionInstance(404,e.getMessage(),null);
        } catch (RuntimeException e){
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }


    @ResponseBody
    @RequestMapping(value = "/deleteFile",method = RequestMethod.GET)
    public ResponseMessage delete(String destination, HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            FileServiceImpl service = FileServiceImpl.getInstance(destination, loginService.getUser().getUSER_ID());
            service.deleteFileMap();
            return ResponseMessage.getSuccessInstance(200,"文件删除成功",null);
        } catch (FileNotFoundException e) {
            return ResponseMessage.getExceptionInstance(404,"文件不存在",null);
        } catch (IOException e){
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        } catch (RuntimeException e){
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "getDir",method = RequestMethod.GET)
    public ResponseMessage getDir(String destination, HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            FileServiceImpl service = FileServiceImpl.getInstance(destination, loginService.getUser().getUSER_ID());
            Map map = service.getDirectory();
            return ResponseMessage.getSuccessInstance(200,"路径获取成功",map);
        } catch (FileNotFoundException e) {
            return ResponseMessage.getExceptionInstance(404,"文件不存在",null);
        }  catch (NoSuchFileException e){
            return ResponseMessage.getExceptionInstance(300,"目标不是文件夹",null);
        } catch (RuntimeException e){
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        }  catch (Exception e){
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "searchFile",method = RequestMethod.GET)
    public ResponseMessage searchDir(String destination,
                                            String fileName,
                                            String type,
                                            HttpServletRequest request,
                                            HttpServletResponse response){
        FileServiceImpl service = null;
        if(destination == null){
            destination = "/";
        }
        try {
            UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
            service = FileServiceImpl.getInstance(destination, loginService.getUser().getUSER_ID());
            FileCollection collection = service.searchFile(fileName,type);
            HashMap map = new HashMap();
            map.put("directory",collection.getDir());
            map.put("file",collection.getFile());
            map.put("code",collection.getCode());
            map.put("message",collection.getMsg());
            return ResponseMessage.getSuccessInstance(200,"搜索成功",map);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseMessage.getSuccessInstance(300,e.getMessage(),null);
        } catch (RuntimeException e){
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Exception e) {
            System.out.println(getTime() + "   ->   未捕获异常: ");
            System.out.println("_______________________________>");
            e.printStackTrace();
            System.out.println("<_______________________________");
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }
    @ResponseBody
    @RequestMapping(value = "shareFile",method = RequestMethod.GET)
    public ResponseMessage shareFile(String destination, @Nullable String second,@Nullable String key,boolean getRandom, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
        FileServiceImpl service = null;
        try {
            UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
            service = FileServiceImpl.getInstance(destination, loginService.getUser().getUSER_ID());
        } catch (FileNotFoundException e) {
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        }
        try {
            int time = 900;
            if (second!=null) {
                time = Integer.parseInt(second);
            }
            if (key == null && getRandom) {
                key = RandomString.ranStr(6);//随机生成提取码
            }
            String url = service.getDownloadURL(time,key);
            String date = getTime(System.currentTimeMillis() + time * 1000);
            HashMap hashMap = new HashMap();
            hashMap.put("downloadURL",url);//token
            hashMap.put("destination",destination);//名字
            hashMap.put("efficient time",date);
            hashMap.put("key",key);
            return ResponseMessage.getSuccessInstance(200,"链接获取成功",hashMap);
        } catch (RuntimeException e){
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Exception e) {
            System.out.println(getTime() + "   ->   未捕获异常: ");
            System.out.println("_______________________________>");
            e.printStackTrace();
            System.out.println("<_______________________________");
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/compression",method = RequestMethod.GET)
    public ResponseMessage Compress(String destination,HttpServletResponse response,HttpServletRequest request){
        if(destination == null){
            return ResponseMessage.getExceptionInstance(300,"目标未找到",null);
        }
        FileServiceImpl service = null;
        try {
            UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
            service = FileServiceImpl.getInstance(destination, loginService.getUser().getUSER_ID());
            service.deCompress();
            return ResponseMessage.getSuccessInstance(200,"文件压缩成功",null);
        } catch (FileNotFoundException e) {
            return ResponseMessage.getExceptionInstance(300,"指定压缩文件未找到",null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/deCompression",method = RequestMethod.GET)
    public ResponseMessage deCompress(String destination,HttpServletResponse response,HttpServletRequest request){
        FileServiceImpl service = null;
        try {
            UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
            service = FileServiceImpl.getInstance(destination, loginService.getUser().getUSER_ID());
            service.deCompress();
            return ResponseMessage.getSuccessInstance(200,"文件解压成功",null);
        } catch (FileNotFoundException e) {
            return ResponseMessage.getExceptionInstance(300,"指定解压文件未找到",null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }
}
