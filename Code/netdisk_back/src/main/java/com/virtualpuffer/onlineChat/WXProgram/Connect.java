package com.virtualpuffer.onlineChat.WXProgram;


import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.controller.WXController.WXLogin;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.Map;

public class Connect {
    public static final String APPID = "wx731ae2faa3297dec";
    public static final String APPSECRET = "34e384be70a2129c41eba756d954c419";
    //获取openid
    public static Map<String, Object> getWxUserOpenid(String code) {
        //拼接url
        StringBuilder url = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code");
        url.append("&appid=");//appid设置
        url.append(APPID);
        url.append("&secret=");//secret设置
        url.append(APPSECRET);
        url.append("&js_code=");//code设置
        url.append(code);
        Map<String, Object> map = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();//构建一个Client
            HttpGet get = new HttpGet(url.toString());    //构建一个GET请求
            HttpResponse response = client.execute(get);//提交GET请求
            HttpEntity result = response.getEntity();//拿到返回的HttpResponse的"实体"
            String content = EntityUtils.toString(result);
            System.out.println(content);//打印返回的信息
            map = JSON.parseObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
