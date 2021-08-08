package com.virtualpuffer.netdisk.Security.securityFilter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.http.MediaType;
import org.springframework.jca.context.SpringContextResourceAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.Map;



@WebFilter(urlPatterns = "/login",filterName = "userLoginFilter")
public class LoginMessageFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if(request.getServletPath().equals("/login")){
            System.out.println("?");
        }


        if(!request.getMethod().equals("POST")){
            response.setStatus(200);
            response.addHeader("Content-Encoding","UTF-8");
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write("老子要post");
            return;
        }

        String type = request.getContentType();
        if(type.equals(MediaType.APPLICATION_JSON_VALUE)||type.equals(MediaType.APPLICATION_JSON_UTF8_VALUE)){
/*            String json = getStringFromInputStream(request.getInputStream());
            Map map = (Map)JSONObject.parse(json);
            System.out.println( "map     " +  map.get("username"));*/
            System.out.println("??");
            response.addHeader("Token","43243242");
        }

        //放行
        filterChain.doFilter(request,response);

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

}
