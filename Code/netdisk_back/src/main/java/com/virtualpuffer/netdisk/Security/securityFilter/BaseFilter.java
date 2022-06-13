package com.virtualpuffer.netdisk.Security.securityFilter;

import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Properties;

@Component
public abstract class BaseFilter implements Filter{
    protected static final String properties = "getMess.properties";
    protected static final String ChineseProperties = "ChineseMess.properties";
    private static Properties get;
    private static Properties property;
    @Autowired
    RedisUtil redisUtil;

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void destroy() {

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
    protected static String getMess(String source) {
        return get.getProperty(source);
    }

    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    static {
        InputStreamReader reader = null;
        if(property == null){
            try {
                property = new Properties();
                reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(ChineseProperties), "UTF-8");
                property.load(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                close(reader);
            }
        }
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(properties);
        if(get == null){
            try {
                get = new Properties();
                get.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                close(in);
            }
        }
    }
}
