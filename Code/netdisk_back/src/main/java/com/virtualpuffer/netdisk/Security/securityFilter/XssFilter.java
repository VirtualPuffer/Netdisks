package com.virtualpuffer.netdisk.Security.securityFilter;

import com.virtualpuffer.netdisk.Security.XssProtection;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/ap1i/*",filterName = "xssFilter")
public class XssFilter extends BaseFilter{

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        if(request.getMethod().equals("GET")){
            XssProtection protection = new XssProtection(request);
            filterChain.doFilter(protection,servletResponse);
        }else {
            filterChain.doFilter(request,response);
        }
    }
}
