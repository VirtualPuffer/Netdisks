package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.service.impl.file.URLFileService;
import com.virtualpuffer.netdisk.utils.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
}
