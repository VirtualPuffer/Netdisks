/*package com.unbelievable.gangballs.controller;

import service.verCodeParse;
import util.Log;
import util.Message;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//配置拦截路径
@WebFilter(filterName = "sourceFilter",urlPatterns = {"/getDir"})
public class parameterFilter implements Filter{
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {

        }
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            //处理参数
            String source = request.getParameter("source");
            System.out.println(Log.getTime() +" ->  source : " + request.getParameter("source") );
            //执行
            filterChain.doFilter(servletRequest, servletResponse);
        }
        @Override
        public void destroy() {

        }
    private static void security(String path) throws RuntimeException{
        new File(path).getName().equals(path);
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
        if(source == null){
            source="/";
        }
        security(source);
        source = URLDecoder.decode(source);
        String verCode = getCookie(request,"verCode");
        //找不到name时会直接上抛异常
        String user_id = verCodeParse.getName(verCode);
        String fileDir = Message.getMess("defaultWare");
        return fileDir + user_id + source;
    }
    public static String StringFilterUtil(String get){
        String ill = "[ !@#$%^&*()_+-={};':,./<>?！@#￥%……&*（）]";
        Pattern pattern = Pattern.compile(ill);
        Matcher matcher = pattern.matcher(get);
        return matcher.replaceAll("").trim();
    }
}*/
