package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.controller.base.BaseController;
import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.impl.file.FileBaseService;
import com.virtualpuffer.netdisk.service.impl.user.UserServiceImpl;
import com.virtualpuffer.netdisk.service.impl.user.UserTokenService;
import com.virtualpuffer.netdisk.utils.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController extends BaseController {
    @ResponseBody
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseMessage doLogin(@RequestBody User user, HttpServletRequest request , HttpServletResponse response){
        try {
            user.setIp((String) request.getAttribute("ip"));
            UserServiceImpl service = UserServiceImpl.getInstance(user,request);
            HashMap hashMap = new HashMap();
            hashMap.put("token",service.getUser().getToken(UserServiceImpl.LOGIN_TAG));//token
            hashMap.put("name",service.getUser().getName());//名字
            Cookie cookie = new Cookie(RemoteResourcesController.remoteCookie,"arrf");
            response.addCookie(cookie);
            return ResponseMessage.getSuccessInstance(200,"登录成功",hashMap);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseMessage.getSuccessInstance(300,e.getMessage(),null);
        } catch (Throwable e){
                e.printStackTrace();//打印异常情况
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }
    @RequestMapping(value = "/salt")
    @ResponseBody
    public ResponseMessage getSalt(HttpServletRequest request , HttpServletResponse response){
        String code = String.valueOf(request.hashCode());
        String salt = code.substring(code.length()-8);
        Cookie cookie = new Cookie("salt",salt);
        response.addCookie(cookie);

        return ResponseMessage.getSuccessInstance(200,salt,null);
    }
    //三个参数：username,password,name
    @ResponseBody
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public ResponseMessage doRegister(@RequestBody User user, HttpServletRequest request , HttpServletResponse response){
        try {
            UserServiceImpl.registerUser(user);
            FileBaseService.getInstance("",UserServiceImpl.getInstance(user,request).getUser(),4)
                    .mkdir(FileOperationController.head_destination,4);
            return ResponseMessage.getSuccessInstance(200,"注册成功",null);
        } catch (RuntimeException e) {
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Throwable e){
                e.printStackTrace();//打印异常情况
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    public ResponseMessage doLogout(HttpServletRequest request , HttpServletResponse response){
        try {
            String token = request.getHeader("Authorization");
            UserTokenService.userLogout(token);
            return ResponseMessage.getSuccessInstance(200,"退出成功",null);
        } catch (RuntimeException e) {
            return ResponseMessage.getSuccessInstance(300,e.getMessage(),null);
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
            hashMap.put("token",service.getUser().getToken(UserServiceImpl.LOGIN_TAG));//token
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
    @RequestMapping(value="/find",method = RequestMethod.POST)
    public ResponseMessage findback(@RequestBody User user, HttpServletRequest request , HttpServletResponse response){
        try {
            UserServiceImpl userService =  UserServiceImpl.getInstanceByAddr(user.getAddr());
            userService.sendResetMail();
            return ResponseMessage.getSuccessInstance(200,"邮件发送成功",null);
        } catch (RuntimeException e) {
            return ResponseMessage.getExceptionInstance(300,e.getMessage(),null);
        } catch (Throwable e){
            e.printStackTrace();//打印异常情况
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }
    @RequestMapping(value = "/resetPassword",method = RequestMethod.POST)
    public Object ki(@RequestBody User user,String key, HttpServletResponse response) throws IOException {
        try {
            UserTokenService service = UserTokenService.getInstanceByToken(user.getToken(),"ip");
            service.resetPassword(user.getPassword());
            return ResponseMessage.getSuccessInstance(200,"密码重置成功",null);
        }catch (ExpiredJwtException e){
            return ResponseMessage.getExceptionInstance(300,"链接已失效",null);
        }catch (JwtException e){
            return ResponseMessage.getExceptionInstance(300,"链接错误 : " + e.getMessage(),null);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseMessage.getErrorInstance(500, "链接错误 : " + e.getMessage(), null);
        }
    }

}
