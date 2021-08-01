package com.virtualpuffer.netdisk.controller;

import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.controller.base.BaseController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.zip.ZipException;


@org.springframework.stereotype.Controller
@RestController
public class FileSearch extends BaseController {
    private static final int BUFFER_SIZE = 4 * 1024;
    private static final String unknow_error = "未知错误";
    private static final String filr_notFound = "文件不存在";
    private static final String zip_Error = "解压失败";
    private static final String illegal_request = "非法访问";
    /*
    * source:static/
    * verCode:url
    * source:/asd
    * */
    private static void close(Closeable cos){
        try {
            if(cos!=null){
                cos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void security(String path) throws RuntimeException{
      /*  new File(path).getCanonicalPath();*/
        if(path.contains("..")){
            throw new RuntimeException("unsecurity path");
        }
        return ;
    }
    public static String getCookie(HttpServletRequest request,String cookieName) throws RuntimeException{
        Cookie[] cook = request.getCookies();
        for(Cookie getCook : cook){
            if(getCook.getName().equals(cookieName)){
                return getCook.getValue();
            }
        }
        throw  new RuntimeException("cookie not found");
    }
    private static String getPath(String source, HttpServletRequest request) throws RuntimeException{
        /*if(source == null){
            source="/";
        }*/
        security(source);
        source = URLDecoder.decode(source);
        String verCode = getCookie(request,"verCode");
        //找不到name时会直接上抛异常
        String user_id = verCodeParse.getName(verCode);
        String fileDir = Message.getMess("defaultWare");
        return fileDir + user_id + source;
    }

    @ResponseBody
    @RequestMapping(value = "/Extract?{fileCode}")
    public static String test(@PathVariable String fileCode){

        System.out.println(fileCode);
        return fileCode;
    }


    @RequestMapping("/getDir")
    public static Object getDir(String source, HttpServletRequest request,HttpServletResponse response){
        String path = source;
        try {
            path = getPath(source,request);
        } catch (RuntimeException e) {
            response.setStatus(200);
            return JSON.toJSON(new DirectData(illegal_request,403));
        }
        System.out.println(Log.getTime() + ": getDir : path : " + path);
        System.out.println("sourcre : " + source);
        File on = new File(path);
        if(!on.exists()){//路径不存在
            response.setStatus(200);
            return JSON.toJSON(new DirectData("文件夹不存在",404));
        }
        if(!on.isDirectory()){//目标路径不是目录
            response.setStatus(200);
            return JSON.toJSON(new DirectData("目标不是文件目录",404));
        }
        File[] get = on.listFiles();
        /*if(get.length == 0){//文件是空
            response.setStatus(200);
            DirectData ret = new DirectData(get,"文件夹是空的",11000);
            return JSON.toJSON(ret);
        }*/
        long size = on.length();
        if(on.isDirectory()){
            response.setStatus(200);
            DirectData ret = new DirectData(get,"路径获取成功",200);
            return JSON.toJSON(ret);
        }
        response.setStatus(500);
        return JSON.toJSON(new DirectData(unknow_error,500));
    }

    @RequestMapping("/searchDir")
    public static Object searchDir(String source, HttpServletRequest request,HttpServletResponse response){
        System.out.println(Log.getTime() + "  deleteFile ->  source " + source);
        String name = source;
        String path = null;
        try {
            path = getPath("",request);
        } catch (RuntimeException e) {
            response.setStatus(200);
            return JSON.toJSON(new DirectData(illegal_request,403));
        }
        try {
            File on = new File(path);
            return JSON.toJSON(new SearchFile(on,name,path));
        } catch (Exception e) {
            response.setStatus(500);
            System.out.println(e);
            return JSON.toJSON(new DirectData(unknow_error,500));
        }
    }


    /*
    * 注册时创建仓库,存在直接返回
    * */
    public static boolean registerBuild(String url){
        String fileDir = Message.getMess("defaultWare");
        String path = fileDir + url;
        File on = new File(path);
        if(on.exists() || on.mkdir()){
            return true;
        }
        return false;
    }

    public static boolean registerBuild(int url){
        return registerBuild(String.valueOf(url));
    }

    /*
    * 是文件则后序遍历删除，否则直接删除
    * */
    public static void delete(File on){
        if(on.isDirectory()){
            File[] get = on.listFiles();
            if(get.length!=0){
                for(File a : get){
                    delete(a);
                }
            }
            on.delete();
        }else {
            on.delete();
        }
    }


    @RequestMapping("/buildDir")
    public static Object build(String source, HttpServletRequest request, HttpServletResponse response){
        String path;
        try {
            path = getPath(source,request);
        } catch (RuntimeException e) {
            return JSON.toJSON(new BuildResponse(illegal_request,403));
        }
        File on = new File(path);
        if(on.exists()){
            response.setStatus(200);
            return JSON.toJSON(new BuildResponse("文件夹已存在",404));
        }
        if(on.mkdir()){
            response.setStatus(200);
            return JSON.toJSON(new BuildResponse("创建成功",source,200));
        }
        response.setStatus(500);
        return JSON.toJSON(new BuildResponse(unknow_error,500));
    }


    @RequestMapping("/deleteDir")
    public static Object delete(String source, HttpServletRequest request, HttpServletResponse response){
        System.out.println(Log.getTime() + "  deleteFile ->  source " + source);
        String path;
        try {
            path = getPath(source,request);
        } catch (RuntimeException e) {
            return JSON.toJSON(new BuildResponse(illegal_request,403));
        }
        File on = new File(path);
        if(!on.exists()){
            response.setStatus(404);
            return JSON.toJSON(new BuildResponse("目标不存在",404));
        }
        delete(on);
        if(!on.exists()){
            response.setStatus(200);
            return JSON.toJSON(new BuildResponse("删除成功",source,200));
        }
        response.setStatus(500);
        return JSON.toJSON(new BuildResponse(unknow_error,500));
    }
    @RequestMapping("/deCompression")
    public static Object deCompress(String source,String destPath, HttpServletRequest request, HttpServletResponse response){
        String path;
        try {
            path = getPath(source,request);
        } catch (RuntimeException e) {
            return JSON.toJSON(new BuildResponse(illegal_request,403));
        }
        File on = new File(path);
        if(!on.exists()){
            response.setStatus(404);
            return JSON.toJSON(new BuildResponse("目标不存在",404));
        }
        try {
            destPath = getPath(destPath,request);
        } catch (RuntimeException e) {
            return JSON.toJSON(new BuildResponse("解压路径错误",404));
        }
        try {
            Compress.deCompress(on,destPath);
            return JSON.toJSON(new BuildResponse("解压成功",200));
        } catch (ZipException e) {
            return JSON.toJSON(new BuildResponse(zip_Error,200));
        } catch (FileNotFoundException e){
            return JSON.toJSON(new BuildResponse("目标不存在",404));
        } catch (IOException e){
            return JSON.toJSON(new BuildResponse(zip_Error + " : io",200));
        }
    }

    @RequestMapping("/downLoadFile")
    public static Object getDownload(String source, HttpServletRequest request, HttpServletResponse response) {
        System.out.println(Log.getTime() + "  downloadFile ->  source " + source);
        if(source == null){
            response.setStatus(200);
            return JSON.toJSON(new BuildResponse("source is null",404));
        }
        String path;
        try {
            path = getPath(source,request);
            System.out.println(Log.getTime() + "  downloadFile ->  path " + path);
        } catch (RuntimeException e) {
            System.out.println(e);
            return JSON.toJSON(new BuildResponse(illegal_request,404));
        }
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        File file = new File(path);
        if(!file.isFile()){//文件____________记得改
            return JSON.toJSON(new BuildResponse("不是文件",404));
        }
        String fileName = path.substring(path.lastIndexOf("/"));//通过最后的‘/’截取文件名
        if (file.isFile()) {
            String contentLength = String.valueOf(file.length());
            response.addHeader("Content-Length",contentLength);
            response.addHeader("Content-Encoding","UTF-8");
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName));
            byte[] buffer = new byte[BUFFER_SIZE];
            try {
                boolean ret = file.exists();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream outputStream = response.getOutputStream();
                int status = bis.read(buffer);
                while (status != -1) {
                    outputStream.write(buffer,0,status);
                    status = bis.read(buffer);
                }
                response.setStatus(200);
                return JSON.toJSON(new BuildResponse("下载成功",200));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {//关流
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        response.setStatus(500);
        return JSON.toJSON(new BuildResponse(unknow_error,500));
    }

    @RequestMapping("/downLoadZipFile")
    public static Object getZipDownload(String source, HttpServletRequest request, HttpServletResponse response) {
        System.out.println(Log.getTime() + "  downloadZipFile ->  source " + source);
        if(source == null){
            response.setStatus(200);
            return JSON.toJSON(new BuildResponse("source is null",404));
        }
        String path;
        try {
            path = getPath(source,request);
            System.out.println(Log.getTime() + "  downZiploadFile ->  path " + path);
        } catch (RuntimeException e) {
            return JSON.toJSON(new BuildResponse(illegal_request,404));
        }
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        File file = new File(path);
        System.out.println(file.getPath());
        String fileName = path.substring(path.lastIndexOf("/"));//通过最后的‘/’截取文件名//通过最后的‘/’截取文件名
        if(!file.exists()){
            return JSON.toJSON(new BuildResponse("file not found",404));
        }
            response.addHeader("Content-Length",String.valueOf(count(path)));
            response.addHeader("Content-Encoding","UTF-8");
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            try {
                OutputStream outputStream = response.getOutputStream();
                response.setStatus(200);
                return JSON.toJSON(new BuildResponse("压缩文件下载成功",200));
            } catch (Exception e) {
            }
        response.setStatus(500);
        return JSON.toJSON(new BuildResponse(unknow_error,500));
    }


    @RequestMapping("/uploadFile")
    public Object UploadTheme(String source,MultipartFile getFile,HttpServletRequest request, HttpServletResponse response){
        System.out.println(Log.getTime() + "  uploadFile ->  source " + source);
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        if(source == null){
            response.setStatus(200);
            return JSON.toJSON(new BuildResponse("source is null",404));
        }
        if(getFile == null){
            response.setStatus(200);
            return JSON.toJSON(new BuildResponse("file is null",404));
        }
        String path;
        try {
            path = getPath(source,request) +"/" + getFile.getOriginalFilename();
        } catch (RuntimeException e) {
            return JSON.toJSON(new BuildResponse(illegal_request,404));
        }

        //重复判断
        if(new File(path).exists()){
            path = path + "(1)";
        }
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            inputStream = getFile.getInputStream();
            outputStream = new FileOutputStream(path);
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return JSON.toJSON(new BuildResponse("上传成功",200));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(inputStream);
            close(outputStream);
        }
        return JSON.toJSON(new BuildResponse(unknow_error,500));
    }


    @RequestMapping("/upLoadFile")
    public static Object upLoad(String source,HttpServletRequest request,HttpServletResponse response){

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        if(source == null){
            response.setStatus(200);
            return JSON.toJSON(new BuildResponse("source is null",404));
        }
        String path;
        try {
            path = getPath(source,request);
        } catch (RuntimeException e) {
            return JSON.toJSON(new BuildResponse(illegal_request,404));
        }
        String fileName = path.substring(path.lastIndexOf("/"),path.length()-1);
        File file = new File(path);
        //重复判断
        if(new File(path).exists()){
            path = path + "(1)";
        }
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            inputStream = request.getInputStream();
            outputStream = new FileOutputStream(path);
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return JSON.toJSON(new BuildResponse("上传成功",200));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(inputStream);
            close(outputStream);
        }
        return JSON.toJSON(new BuildResponse(unknow_error,500));
    }
}

class BuildResponse{
    int code;
    String msg;
    String URL;

    public BuildResponse(String msg, String URL,int code) {
        this.msg = msg;
        this.URL = URL;
        this.code = code;
    }

    public BuildResponse(String msg,int code) {
        if(code == 500){
            Log.getLog().systemLog();
        }
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public String getURL() {
        return URL;
    }
    public int getCode() {
        return code;
    }
}

class SearchFile{
    LinkedList<String> dir;
    LinkedList<String> files;
    int code = 200;
    String msg;
    public SearchFile(File file,String name,String path){
        this.dir = new LinkedList<String>();
        this.files = new LinkedList<String>();
        search(file,name,path);
        if(dir.isEmpty()&&files.isEmpty()){
            msg = "文件未找到";
        }else {
            msg = "成功";
        }
    }

    public void search(File file, String name,String path){
        //文件本身
        if(file.getName().matches("(?i).*"+ name +".*")){
            String retPath = file.getPath().substring(path.length());
            dir.add("/" + retPath);
        }
        //子目录分析
        File[] fileArr = file.listFiles();
        for(File get : fileArr){
            //递归
            if(get.isDirectory()){
                search(get,name,path);
            }
            //分析
            if(get.getName().matches("(?i).*"+ name +".*") && get.isFile()){
                String retPath = get.getPath().substring(path.length());
                files.add("/" + retPath);
            }
        }

    }
    //文件映射，返回dir和file名字，反射的时候，获取的是get的字段
    public int getCode() {
        return code;
    }
    public LinkedList<String> getDir() {
        return dir;
    }

    public LinkedList<String> getFile() {
        return files;
    }

    public String getMsg() {
        return msg;
    }
}
