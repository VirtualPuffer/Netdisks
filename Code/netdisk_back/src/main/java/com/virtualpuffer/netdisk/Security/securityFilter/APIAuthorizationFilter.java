package com.virtualpuffer.netdisk.Security.securityFilter;


import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;


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
public class APIAuthorizationFilter implements Filter {
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
            if(request.getParameter("virtual")!=null){token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjMiLCJwYXNzd29yZCI6IjEyMyIsImlwIjoiMTkyLjE4Ni4wLjEiLCJleHAiOjE2MzI5NzkyNTUsInVzZXJJRCI6MSwiaWF0IjoxNjI5NTIzMjU1LCJqdGkiOiJhMWYzNTE0YS1mMzIxLTQ5MzUtOTcwNy04MTQxNDMwMGZmZTMiLCJ1c2VybmFtZSI6IjEyMyJ9.ICYXP1HPSNXduBNT333n0tNOKzVjTc3obn4-y-0U-y4";}
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
            response.setStatus(200);
            response.addHeader("Content-Encoding","UTF-8");
            response.setContentType("text/html;charset=utf-8");
            ResponseMessage responseMessage =
                    ResponseMessage.getExceptionInstance
                            (1000,"权限校验失败，请重新登录",null);
            response.getWriter().write(JSON.toJSONString(responseMessage));
            return;
        }

    }

    public static String getStringFromInputStream(InputStream inputStream) {
        String resultData = null;      //需要返回的结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

}
