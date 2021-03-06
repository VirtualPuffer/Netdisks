package com.virtualpuffer.netdisk.Security.securityFilter;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@WebFilter(urlPatterns = "/user/login",filterName = "usernameLoginFilter")
public class LoginFilter extends BaseFilter{

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.getAttribute("ip");
        filterChain.doFilter(request,response);
    }
}
