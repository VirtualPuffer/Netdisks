package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.service.impl.file.URLFileService;
import com.virtualpuffer.netdisk.utils.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@RestController
@RequestMapping("/wife")
public class WifeController {
    public static final String location = "/usr/local/MyTomcat/wife/";
    @RequestMapping("/pullWife")
    public ResponseMessage DownloadWife(String url) throws IOException {
        try {
            try {
                URLFileService.httpDownload(url);
                return ResponseMessage.getSuccessInstance(200,"下载成功",null);
            } catch (IOException e) {
                URLFileService.httpsDownload(url);
                return ResponseMessage.getSuccessInstance(200,"下载成功",null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500,"下载失败",null);
        }
    }
    @RequestMapping("/pullHttpsWife")
    public ResponseMessage DownloadHttpsWife(String url) throws IOException {
        try {
                URLFileService.httpsDownload(url);
                return ResponseMessage.getSuccessInstance(200,"下载成功",null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500,"下载失败",null);
        }
    }
    @ResponseBody
    @RequestMapping("/getAllWife")
    public ResponseMessage getAllWife(){
        try {
            Map a = new HashMap();
            a.put("wife",URLFileService.getPicture());
            return ResponseMessage.getSuccessInstance(200,"获取成功",a);
        } catch (Exception e) {
            return ResponseMessage.getErrorInstance(404,"获取失败",null);
        }
    }
    @ResponseBody
    @RequestMapping("/getPC")
    public ModelAndView getpc(HttpServletResponse response){
        response.setHeader("Cache-Control","max-age=999999999");
        File f = new File("/usr/local/MyTomcat/wife/PC");
        File[] r = f.listFiles();
        Random getRan = new Random();
        int index = getRan.nextInt(r.length);
        ModelAndView view = new ModelAndView("/PC/" + r[index].getName());
        //view.setStatus(HttpStatus.NOT_MODIFIED);
        return view;
    }
    @ResponseBody
    @RequestMapping("/getPF")
    public ModelAndView getp(HttpServletResponse response){
        //response.setHeader("Cache-Control","max-age=999999999");
        File f = new File("/usr/local/MyTomcat/wife/PC");
        File[] r = f.listFiles();
        Random getRan = new Random();
        int index = getRan.nextInt(r.length);
        ModelAndView view = new ModelAndView("/PC/" + r[index].getName());
        //view.setStatus(HttpStatus.NOT_MODIFIED);
        return view;
    }
    @ResponseBody
    @RequestMapping("/getPE")
    public ModelAndView getPE(HttpServletResponse response){
        response.setHeader("Cache-Control","max-age=999999999");
        File f = new File("/usr/local/MyTomcat/wife/PE");
        File[] r = f.listFiles();
        Random getRan = new Random();
        int index = getRan.nextInt(r.length);
        ModelAndView view = new ModelAndView("/PE/" + r[index].getName());
        //view.setStatus(HttpStatus.NOT_MODIFIED);
        return view;
    }
}
