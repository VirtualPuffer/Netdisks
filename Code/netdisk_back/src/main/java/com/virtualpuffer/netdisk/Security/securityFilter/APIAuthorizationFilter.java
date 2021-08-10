package com.virtualpuffer.netdisk.Security.securityFilter;


import com.virtualpuffer.netdisk.service.impl.UserServiceImpl;
import org.springframework.http.MediaType;


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
@WebFilter(urlPatterns = "/api/*",filterName = "apiControlFilter")
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

        String type = request.getContentType();
        if(type.equals(MediaType.APPLICATION_JSON_VALUE)||type.equals(MediaType.APPLICATION_JSON_UTF8_VALUE)){

        }
        try {
            //解析token
            String token = request.getHeader("Authorization");
            System.out.println(token);
            if(token != null && !token.equals("") ){
                String ip = (String) request.getAttribute("ip");
                UserServiceImpl service = UserServiceImpl.getInstance(token,ip);
                request.setAttribute("AuthService",service);
                request.getAttribute("AuthService");
            }else {
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
            response.setStatus(200);
            response.addHeader("Content-Encoding","UTF-8");
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write("权限校验失败，请重新登录");
            return;
        }
        //放行
        filterChain.doFilter(request,response);
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
