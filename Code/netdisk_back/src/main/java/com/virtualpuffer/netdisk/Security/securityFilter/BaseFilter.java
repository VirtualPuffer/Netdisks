package com.virtualpuffer.netdisk.Security.securityFilter;

import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public abstract class BaseFilter implements Filter{

    @Autowired
    RedisUtil redisUtil;

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void destroy() {

    }

    public static String getStringFromInputStream(InputStream inputStream) {
        String resultData = null;      //需要返回的结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }
    public void buildMessage(HttpServletResponse response,ResponseMessage responseMessage) throws IOException {
        response.setStatus(200);
        response.addHeader("Content-Encoding","UTF-8");
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(responseMessage));
    }

    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }
}
