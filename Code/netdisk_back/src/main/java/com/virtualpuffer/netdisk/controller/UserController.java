package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.controller.base.BaseController;
import com.virtualpuffer.netdisk.data.ResponseMessage;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.impl.UserServiceImpl;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
                UserServiceImpl service = UserServiceImpl.getInstance(token,ip);
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

}
