package com.virtualpuffer.netdisk.Security.messageHandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.buf.CharsetUtil;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Console;
import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private ObjectMapper mapper;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }
    //成功逻辑
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
       String res = "";
        response.setStatus(HttpServletResponse.SC_OK);
       response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
       response.getWriter().write(mapper.writeValueAsString(authentication));
    }
}
