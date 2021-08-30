package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.controller.base.BaseController;
import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.impl.file.FileServiceImpl;
import com.virtualpuffer.netdisk.service.impl.file.FileTokenService;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/user")
public class UserController extends BaseController {
    private User user;

    @ResponseBody
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseMessage doLogin(@RequestBody User user, HttpServletRequest request , HttpServletResponse response){
        try {
            user.setIp((String) request.getAttribute("ip"));
            UserServiceImpl service = UserServiceImpl.getInstance(user,request);
            HashMap hashMap = new HashMap();
            hashMap.put("token",service.getUser().getToken());//token
            hashMap.put("name",service.getUser().getName());//名字
            return ResponseMessage.getSuccessInstance(200,"登录成功",hashMap);
        } catch (RuntimeException e) {
            return ResponseMessage.getSuccessInstance(300,e.getMessage(),null);
        } catch (Throwable e){
                e.printStackTrace();//打印异常情况
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }

    //三个参数：username,password,name
    @ResponseBody
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public ResponseMessage doRegister(@RequestBody User user, HttpServletRequest request , HttpServletResponse response){
        try {
            UserServiceImpl.registerUser(user);
            return ResponseMessage.getSuccessInstance(200,"注册成功",null);
        } catch (RuntimeException e) {
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Throwable e){
                e.printStackTrace();//打印异常情况
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }

    @ResponseBody
    @RequestMapping(value="freeLogin",method = RequestMethod.POST)
    public ResponseMessage freeLogin(@RequestBody User user, HttpServletRequest request , HttpServletResponse response){
        try {
            String token = request.getHeader("Authorization");
            if(token!=null){
                String ip = (String) request.getAttribute("ip");
                UserServiceImpl service = (UserServiceImpl) UserTokenService.getInstanceByToken(token,ip);
                request.setAttribute("AuthService",service);
                request.getAttribute("AuthService");
            }
            user.setIp((String) request.getAttribute("ip"));
            UserServiceImpl service = UserServiceImpl.getInstance(user,request);
            HashMap hashMap = new HashMap();
            hashMap.put("token",service.getUser().getToken());//token
            hashMap.put("name",service.getUser().getName());//名字
            return ResponseMessage.getSuccessInstance(200,"登录成功",hashMap);
        } catch (RuntimeException e) {
            return ResponseMessage.getSuccessInstance(300,e.getMessage(),null);
        } catch (Throwable e){
            e.printStackTrace();//打印异常情况
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }

    @ResponseBody
    @RequestMapping(value="/find")
    public ResponseMessage findback(String addr, HttpServletRequest request , HttpServletResponse response){
        try {
            UserServiceImpl userService = UserServiceImpl.getInstanceByAddr(addr);
            userService.sendMess();
            return ResponseMessage.getSuccessInstance(200,"获取成功",null);
        } catch (RuntimeException e) {
            return ResponseMessage.getSuccessInstance(300,e.getMessage(),null);
        } catch (Throwable e){
            e.printStackTrace();//打印异常情况
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }

    @RequestMapping(value = "/resetPassword/{token}")
    public Object i(@PathVariable String token,String key,String password, HttpServletResponse response) throws IOException {
        String ip = (String) request.getAttribute("ip");
        if(key == null || key.equals("")){
            return new ModelAndView("/getURL.html");
        }else {
            InputStream inputStream = null;
            try {
                UserTokenService service = UserTokenService.getInstanceByToken(token,ip);
                service.resetPassword(password);

                return ResponseMessage.getSuccessInstance(200,"密码重置成功",null);
            }catch (ExpiredJwtException e){
                return ResponseMessage.getExceptionInstance(300,"链接已失效",null);
            }catch (IllegalArgumentException e){
                return ResponseMessage.getExceptionInstance(300,"密码错误",null);
            }catch (JwtException e){
                return ResponseMessage.getExceptionInstance(300,"链接错误 : " + e.getMessage(),null);
            }catch (Exception e){
                e.printStackTrace();
                return ResponseMessage.getErrorInstance(500,"下载失败",null);
            }finally {
                close(inputStream);
            }
        }
    }

}
