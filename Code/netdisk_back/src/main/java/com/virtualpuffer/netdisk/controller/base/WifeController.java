package com.virtualpuffer.netdisk.controller.base;

import com.virtualpuffer.netdisk.utils.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

@RequestMapping("/wife")
public class WifeController {
    public static final String location = "/usr/local/MyTomcat/wife";
    @RequestMapping("/pullWife")
    public void downloadWife(String url) throws IOException {

        sendGet("http:/" + StringUtils.filePathDeal(url));
    }
    protected static void copy(InputStream inputStream, OutputStream outputStream)throws IOException{
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
    }
    public static void sendGet(String url) throws IOException {
        URL realUrl = new URL(url);
        URLConnection connection = realUrl.openConnection();
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(15000);
        connection.connect();
        copy(connection.getInputStream(),new FileOutputStream(location + url.substring(url.length() - 10,url.length())));
        return;
    }
}
