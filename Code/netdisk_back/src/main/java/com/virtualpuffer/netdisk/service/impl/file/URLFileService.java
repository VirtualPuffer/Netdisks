package com.virtualpuffer.netdisk.service.impl.file;

import com.virtualpuffer.netdisk.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class URLFileService extends FileUtilService {
    public static final String netdiskContext = getMess("context");
    public static final String location = "/usr/local/MyTomcat/wife";

    public static LinkedList getPicture(){
        File x = new File(location);
        File[] get = x.listFiles();
        LinkedList<String> list = new LinkedList();
        for(File file : get){
            list.add(netdiskContext + file.getName());
        }
        return list;
    }

    public static void httpDownload(String url) throws IOException {
        String get = StringUtils.filePathDeal(url);
        downloadFromURL("http:/" + get);
    }

    public static void httpsDownload(String url) throws IOException {
        String get = StringUtils.filePathDeal(url);
        downloadFromURL("https:/" + get);
    }

    public static void downloadFromURL(String url) throws IOException {
        URL realUrl = new URL(url);
        URLConnection connection = realUrl.openConnection();
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(15000);
        connection.connect();
        copy(connection.getInputStream(),new FileOutputStream(location + "/" +url.substring(url.lastIndexOf("/")+1,url.length())));
        return;
    }
}
