package com.virtualpuffer.netdisk.controller.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.enums.ErrorCode;
import com.virtualpuffer.netdisk.enums.Result;
import com.virtualpuffer.netdisk.service.impl.FileServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BaseController extends BaseLogger {
    @Resource
    protected HttpServletRequest request;
    @Resource
    protected HttpServletResponse response;

    public BaseController() {
    }

    @InitBinder
    protected void initBinder(ServletRequestDataBinder binder) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CustomDateEditor editor = new CustomDateEditor(df, false);
            binder.registerCustomEditor(Date.class, editor);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public static String getTime(long get){
        Date getDate = new Date(get);
        SimpleDateFormat sip = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sip.format(getDate);
    }
    public static String getTime(){
        return getTime(System.currentTimeMillis());
    }

    protected static void close(Closeable cos){
        try {
            if(cos!=null){
                cos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Map<String, String> parseParamsToMap(String paramsStr) {
        return (Map) JSON.parseObject(paramsStr, Map.class);
    }

    protected String parseObjToJsonString(Object obj) {
        return JSON.toJSONString(obj);
    }

    protected Map result(Object data) {
        Map<String, Object> result = new HashMap();
        result.put(Result.success.name(), true);
        result.put(Result.data.name(), data);
        return result;
    }

    protected String error(String message, ErrorCode errorCode) {
        Map<String, Object> result = new HashMap();
        result.put("msg", message);
        result.put("error_code", errorCode);
        return this.toJson(result, (String)null);
    }

    private String toJson(Object obj, String timeFormat) {
        if (StringUtils.isBlank(timeFormat)) {
            JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        }

        SerializerFeature[] serializerFeatures = new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullStringAsEmpty};
        String jsonpCallback = this.request.getParameter("jsonpcallback");
        return !StringUtils.isBlank(jsonpCallback) ? jsonpCallback + "(" + JSON.toJSONString(obj, serializerFeatures) + ")" : JSON.toJSONString(obj, serializerFeatures);
    }


    protected void sendFile(File file, HttpServletResponse response) throws IOException {
        if (file != null && this.response != null) {
            if (file.exists()) {
                this.response.setDateHeader("Last-Modified", file.lastModified());
                this.response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
                this.response.setContentLength((int)file.length());
                FileCopyUtils.copy(new FileInputStream(file), this.response.getOutputStream());
            } else {
                this.response.sendError(404, "找不到所请求的文件！");
            }

            this.response.getOutputStream().flush();
        }
    }

    protected void sendStream(InputStream inputStream, String fileName) throws IOException {
        if (inputStream != null && this.response != null) {
            if (inputStream.available() > 0) {
                this.response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
                this.response.setDateHeader("Last-Modified", (new Date()).getTime());
                this.response.setContentLength(inputStream.available());
                FileCopyUtils.copy(inputStream, this.response.getOutputStream());
            } else {
                this.response.sendError(404, "找不到所请求的数据流！");
            }

            this.response.getOutputStream().flush();

        }
    }

    protected void sendFileStream(User user,String destination,OutputStream outputStream) throws Exception {
        FileServiceImpl fileService = null;
        try {
            fileService = new FileServiceImpl(destination,user);
        } catch (FileNotFoundException e) {
            //找不到
        }
        int length = (int) fileService.downloadFile(outputStream);
        response.setContentType("application/force-download");
        this.response.setContentLength(length);
        this.response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileService.getFile_name(), "UTF-8"));

    }

    protected void sendFileStream(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        if (inputStream != null && this.response != null) {
            if (inputStream.available() > 0) {
                this.response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
                this.response.setDateHeader("Last-Modified", (new Date()).getTime());
                this.response.setContentLength(inputStream.available());
                FileCopyUtils.copy(inputStream, this.response.getOutputStream());
            } else {
                this.response.sendError(404, "找不到所请求的文件流！");
            }

            this.response.getOutputStream().flush();
        }
    }

    protected void sendFileStream(File file, String contentType, String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        if (inputStream != null && this.response != null) {
            if (inputStream.available() > 0) {
                this.response.addHeader("Content-Type", contentType);
                this.response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
                this.response.setDateHeader("Last-Modified", (new Date()).getTime());
                this.response.setContentLength(inputStream.available());
                FileCopyUtils.copy(inputStream, this.response.getOutputStream());
            } else {
                this.response.sendError(404, "找不到所请求的文件流！");
            }

            this.response.getOutputStream().flush();
        }
    }

    protected void sendFileStream(byte[] data, String contentType, String fileName) throws IOException {
        if (data != null && this.response != null) {
            this.response.addHeader("Content-Type", contentType);
            this.response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            this.response.setDateHeader("Last-Modified", (new Date()).getTime());
            this.response.setContentLength(data.length);
            FileCopyUtils.copy(data, this.response.getOutputStream());
        } else {
            this.response.sendError(404, "找不到所请求的文件流！");
        }
        this.response.getOutputStream().flush();
    }
}
