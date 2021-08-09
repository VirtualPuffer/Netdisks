package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.controller.base.BaseController;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.impl.LoginServiceImpl;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController extends BaseController {
    private User user;
    private SqlSession session;

    @ResponseBody
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String doLogin(@RequestBody User user, HttpServletRequest request , HttpServletResponse response){
        try {
            user.setIp((String) request.getAttribute("ip"));
            LoginServiceImpl service = LoginServiceImpl.getInstance(user,request);
            result(service.getUser().getToken());
            return service.getUser().getToken();
        } catch (RuntimeException e) {
            return "";
        }
    }

}
