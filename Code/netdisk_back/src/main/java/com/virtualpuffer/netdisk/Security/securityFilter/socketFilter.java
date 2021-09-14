package com.virtualpuffer.netdisk.Security.securityFilter;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/webSocket",filterName = "usernameLoginFilter111")
public class socketFilter extends BaseFilter{
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String protocol = request.getHeader("Sec-WebSocket-Protocol");
        response.setHeader("Sec-WebSocket-Protocol",protocol);
        filterChain.doFilter(request,response);
    }
}
