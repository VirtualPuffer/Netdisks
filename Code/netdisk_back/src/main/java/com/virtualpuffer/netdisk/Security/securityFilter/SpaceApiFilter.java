package com.virtualpuffer.netdisk.Security.securityFilter;

import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.impl.personal_space.AbstractPersonalSpace;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/ap1i/space/*",filterName = "xeapiControlFilter")
public class SpaceApiFilter extends BaseFilter{
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        UserServiceImpl loginService = (UserServiceImpl) request.getAttribute("AuthService");
        User user = loginService.getUser();
        AbstractPersonalSpace personalSpace = new AbstractPersonalSpace(user);
        if(personalSpace!=null){
            request.setAttribute("space",personalSpace);
            filterChain.doFilter(request,response);
            return;
        }else {
            buildMessage(response, ResponseMessage.getExceptionInstance(404,"该用户尚未注册空间",null));
            return;
        }
    }
}
