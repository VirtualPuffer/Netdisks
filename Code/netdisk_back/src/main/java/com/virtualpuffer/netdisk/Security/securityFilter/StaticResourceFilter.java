package com.virtualpuffer.netdisk.Security.securityFilter;

import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Order(1)
@WebFilter(urlPatterns = "/3231",filterName = "aaageControlFilter")
public class StaticResourceFilter extends BaseFilter{
    private Set<String> URLWhiteList = new HashSet();
    private static final String message = "urlWhiteList";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String[] config = getMess(message).split("\\|");//用\\转义|
        for(String con : config){
            URLWhiteList.add(con);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getServletPath();
        if(URLWhiteList.contains(path)){
            response.setStatus(200);
            return;
        }
        filterChain.doFilter(request,response);
    }
}
