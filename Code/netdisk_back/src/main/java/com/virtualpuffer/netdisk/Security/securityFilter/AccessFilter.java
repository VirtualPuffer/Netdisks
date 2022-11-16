package com.virtualpuffer.netdisk.Security.securityFilter;

import com.virtualpuffer.netdisk.data.ResponseMessage;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebFilter(urlPatterns = "/*",filterName = "Filter0")
public class AccessFilter extends BaseFilter {
    public static boolean ipLimit = false;
    public static final int IPAccessLimit = Integer.parseInt(getMess("IPAccessLimit"));

    public static Set<String> IPWhiteList = new HashSet<>();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String ip = null;
        //添加ip
        if (request.getHeader("x-forwarded-for")==null || request.getHeader("x-forwarded-for").equals(null)) {
            ip = request.getRemoteAddr();
        }else {
            ip = request.getHeader("x-forwarded-for");
        }
        request.setAttribute("ip",ip);
        if(request.getParameter("virtual")!=null){
            filterChain.doFilter(request,response);
            return;
        }
        if(ipLimit && !IPWhiteList.contains(ip)){
            response.setStatus(403);
            return;
        }
        filterChain.doFilter(request,response);
    }
}
