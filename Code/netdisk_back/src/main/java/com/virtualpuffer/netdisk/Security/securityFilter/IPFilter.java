package com.virtualpuffer.netdisk.Security.securityFilter;

import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.utils.RedisUtil;
import org.apache.catalina.util.ParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Order()
@Component
@WebFilter(urlPatterns = "/*",filterName = "aAmessageFilter")
public class IPFilter extends BaseFilter implements Filter {

    public static final int IPAccessLimit = 50;
    @Autowired
    RedisUtil redisUtil;

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
        String ip = null;
        //添加ip
        if (request.getHeader("x-forwarded-for") == null) {
            ip = request.getRemoteAddr();
        }else {
            ip = request.getHeader("x-forwarded-for");
        }
        request.setAttribute("ip",ip);
        String numberOfAccess = redisUtil.getString(ip);
        if(Integer.parseInt(numberOfAccess) > IPAccessLimit){
            //频率过高，拒绝服务
            ResponseMessage responseMessage =
                    ResponseMessage.getExceptionInstance
                            (403,"访问过于频繁，请稍后再次请求",null);
            buildMessage(response,responseMessage);
            return;
        }else if(null == numberOfAccess){
            redisUtil.set(ip,0,1000);
        }else {
            redisUtil.increase(ip);
        }
        filterChain.doFilter(request,response);
    }
}
