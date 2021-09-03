package com.virtualpuffer.netdisk.controller;


import com.virtualpuffer.netdisk.controller.base.BaseController;
import com.virtualpuffer.netdisk.data.FileCollection;
import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.entity.File_Map;
import com.virtualpuffer.netdisk.service.impl.file.FileBaseService;
import com.virtualpuffer.netdisk.service.impl.file.FileHashService;
import com.virtualpuffer.netdisk.service.impl.file.FileTokenService;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.utils.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class FileOperationController extends BaseController {
    public FileOperationController() {
    }

    @ResponseBody
    @RequestMapping(value = "/downloadFile",method = RequestMethod.GET)
    public ResponseMessage get(String destination, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        try {
            FileBaseService service = FileBaseService.getInstance(destination, loginService.getUser().getUSER_ID());
            int length = (int)service.downloadFile(response.getOutputStream());
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(service.getNetdiskFile().getFile_Name(), "UTF-8"));
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
    public ResponseMessage upload(String destination,MultipartFile getFile, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        if(destination == null || destination.equals("")){
            destination = "";
        }
        if(getFile == null){
            return ResponseMessage.getExceptionInstance(404,"未找到上传的文件流",null);
        }
        try {
            String path = destination + "/" + getFile.getOriginalFilename();
            FileBaseService service = FileBaseService.getInstance(path, loginService.getUser().getUSER_ID());
            service.uploadFile(getFile.getInputStream());
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
    @RequestMapping(value = "/uploadHashFile",method = RequestMethod.POST)
    public ResponseMessage uploadHash(String destination,String hash,String name, HttpServletRequest request, HttpServletResponse response){
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        if(destination == null || destination.equals("")){
            destination = "";
        }
        if(hash == null){
            return ResponseMessage.getExceptionInstance(404,"hash为空",null);
        }
        try {
          /*  FileServiceImpl service = FileServiceImpl.getInstanceByHash(hash,name);*/
            FileHashService service = FileHashService.getInstanceByHash(hash,name);
            service.getNetdiskFile().setFile_Destination(destination);
            service.setUser(loginService.getUser());
            service.upLoadByHash();
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
            FileBaseService service = FileBaseService.getInstance(on.getDestination(),loginService.getUser().getUSER_ID());
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
            FileBaseService service = FileBaseService.getInstance(destination, loginService.getUser().getUSER_ID());
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
            FileBaseService service = FileBaseService.getInstance(destination, loginService.getUser().getUSER_ID());
            Map map = service.getDirectory();
            return ResponseMessage.getSuccessInstance(200,"路径获取成功",map);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(404,"文件不存在" + e.getMessage(),null);
        }  catch (NoSuchFileException e){
            return ResponseMessage.getExceptionInstance(300,"目标不是文件夹" + e.getMessage(),null);
        } catch (RuntimeException e){
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        }  catch (Exception e){
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "searchDir")
    public ResponseMessage searchDir(String destination,
                                            String name,
                                            String type,
                                            HttpServletRequest request,
                                            HttpServletResponse response){
        FileBaseService service = null;
        if(destination == null){
            destination = "/";
        }
        try {
            UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
            service = FileBaseService.getInstance(destination, loginService.getUser().getUSER_ID());
            FileCollection collection = service.searchFile(name,type);
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
        FileBaseService service = null;
        try {
            UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
            service = FileBaseService.getInstance(destination, loginService.getUser().getUSER_ID());
        } catch (FileNotFoundException e) {
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        }
        try {
            int time = 900;
            if (second!=null) {
                time = Integer.parseInt(second);
            }
            if (key == null && getRandom) {
                key = StringUtils.ranStr(6);//随机生成提取码
            }
            String url = service.getDownloadURL(time,key, FileBaseService.DOWNLOAD_TAG);
            String date = getTime(System.currentTimeMillis() + time * 1000);
            HashMap hashMap = new HashMap();
            hashMap.put("downloadURL",url);//token
            hashMap.put("destination",destination);//名字
            hashMap.put("efficient time",date);
            hashMap.put("key",key);
            return ResponseMessage.getSuccessInstance(200,"链接获取成功",hashMap);
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
    @RequestMapping(value = "renameFile",method = RequestMethod.GET)
    public ResponseMessage renameFile(String destination,String name, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
        FileBaseService service = null;
        try {
            UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
            service = FileBaseService.getInstance(destination, loginService.getUser().getUSER_ID());
            service.rename(name);
            return ResponseMessage.getSuccessInstance(200,"重命名成功",null);
        } catch (FileNotFoundException e) {
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (RuntimeException e){
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/compression",method = RequestMethod.GET)
    public ResponseMessage Compress(String destination,HttpServletResponse response,HttpServletRequest request){
        if(destination == null){
            return ResponseMessage.getExceptionInstance(300,"目标未找到",null);
        }
        FileBaseService service = null;
        try {
            UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
            service = FileBaseService.getInstance(destination, loginService.getUser().getUSER_ID());
            service.compression();
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
        FileBaseService service = null;
        try {
            UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
            service = FileBaseService.getInstance(destination, loginService.getUser().getUSER_ID());
          /*  service.deCompress();*/
            return ResponseMessage.getSuccessInstance(200,"文件解压成功",null);
        } catch (FileNotFoundException e) {
            return ResponseMessage.getExceptionInstance(300,"指定解压文件未找到",null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/storage",method = RequestMethod.POST)
    public ResponseMessage storage(String destination,String url,HttpServletRequest request,HttpServletResponse response){
        FileBaseService service = null;
        try {
            //用id dest定位文件，url获取源文件位置
            UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
            service = FileTokenService.getInstanceByURL(destination,url,loginService.getUser());
            service.transfer();
            return ResponseMessage.getSuccessInstance(200,"文件储存成功",null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(404,"指定文件未找到",null);
        }catch (RuntimeException e){
            e.printStackTrace();
            return ResponseMessage.getExceptionInstance(600,e.getMessage(),null);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }
}
