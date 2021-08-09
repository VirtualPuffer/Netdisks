package com.virtualpuffer.netdisk.Security.securityFilter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*",filterName = "messageFilter")
public class MessageParse implements Filter {
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
        //添加ip
        if (request.getHeader("x-forwarded-for") == null) {
            request.setAttribute("ip",request.getRemoteAddr());
        }else {
            request.setAttribute("ip",request.getHeader("x-forwarded-for"));
        }
        filterChain.doFilter(request,response);
    }
}
