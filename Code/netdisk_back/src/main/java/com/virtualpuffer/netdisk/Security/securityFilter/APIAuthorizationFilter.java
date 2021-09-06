package com.virtualpuffer.netdisk.Security.securityFilter;


import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;
import com.virtualpuffer.netdisk.utils.Log;
import com.virtualpuffer.netdisk.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
* 通过header传输token,
 * 获取token后在LoginService里注册
 * LoginService检测token真实性
 * 出问题直接抛出异常
* */
@WebFilter(urlPatterns = "/api/*",filterName = "xapiControlFilter")
public class APIAuthorizationFilter extends BaseFilter{

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            String token = request.getHeader("Authorization");
            if(request.getParameter("virtual")!=null){
                token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjMiLCJwYXNzd29yZCI6IjEyMyIsInRva2VuVGFnIjoibG9naW4iLCJpcCI6bnVsbCwiZXhwIjoxNjMxNDYyODY0LCJ1c2VySUQiOjEsImlhdCI6MTYzMDg1ODA2NCwianRpIjoiMjU2NmU1M2YtYzFiMC00YzIyLTkxNWQtM2ZiYWI0NDQ1MTA5IiwidXNlcm5hbWUiOiIxMjMifQ.t5UB-9LOqGzhmdtdtPE5yNT1_LBVgMqU4HD1lUgPN6w";
            }

            String ip = (String) request.getAttribute("ip");
            UserServiceImpl service = UserTokenService.getInstanceByToken(token,ip);
            request.setAttribute("AuthService",service);
            //token是否为登录token以及token有没有过期
            if(service.getTokenTag().equals(UserServiceImpl.LOGIN_TAG) && !UserServiceImpl.TOKEN_EXPIRE.equals(redisUtil.get(token))){
                filterChain.doFilter(request,response);
                return;
            }
            throw new RuntimeException();
        } catch (Exception e) {
            e.printStackTrace();
            ResponseMessage responseMessage =
                    ResponseMessage.getExceptionInstance
                            (1000,"权限校验失败，请重新登录",null);
            buildMessage(response,responseMessage);
            return;
        }
    }
}
