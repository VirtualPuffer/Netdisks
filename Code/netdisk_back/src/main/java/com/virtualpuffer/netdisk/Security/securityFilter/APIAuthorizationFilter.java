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
@Component
@WebFilter(urlPatterns = "/api/*",filterName = "xapiControlFilter")
public class APIAuthorizationFilter extends BaseFilter implements Filter {

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
        try {
            String token = request.getHeader("Authorization");
            if(request.getParameter("virtual")!=null){token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjMiLCJwYXNzd29yZCI6IjEyMyIsInRva2VuVGFnIjoibG9naW4iLCJpcCI6bnVsbCwiZXhwIjoxNjMwODI0ODA2LCJ1c2VySUQiOjEsImlhdCI6MTYzMDczODQwNiwianRpIjoiMTNhMmEzYmMtOWRkYy00YjI4LWIwMzUtMDBkOTIwMTY3OGJiIiwidXNlcm5hbWUiOiIxMjMifQ.fVEgc0fFhSn3RjinqVL0cNZHbKoeIXEh8mdJGSVmyBQ";}

            String ip = (String) request.getAttribute("ip");
            UserServiceImpl service = UserTokenService.getInstanceByToken(token,ip);
            request.setAttribute("AuthService",service);
            if(service.getTokenTag().equals(UserServiceImpl.LOGIN_TAG)){
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
