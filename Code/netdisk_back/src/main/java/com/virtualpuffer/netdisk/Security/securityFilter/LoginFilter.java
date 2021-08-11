package com.virtualpuffer.netdisk.Security.securityFilter;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@WebFilter(urlPatterns = "/user/login",filterName = "usernameLoginFilter")
public class LoginFilter implements Filter {
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

        filterChain.doFilter(request,response);
    }
    public static String StringFilterUtil(String get){
        String ill = "[ !@#$%^&*()_+-={};':,./<>?！@#￥%……&*（）]";
        Pattern pattern = Pattern.compile(ill);
        Matcher matcher = pattern.matcher(get);
        return matcher.replaceAll("").trim();
    }
}
