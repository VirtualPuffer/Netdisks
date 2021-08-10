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
    private SqlSession session;

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
        } catch (Exception e){
            return ResponseMessage.getErrorInstance(500,"系统错误",null);
        }
    }

}
