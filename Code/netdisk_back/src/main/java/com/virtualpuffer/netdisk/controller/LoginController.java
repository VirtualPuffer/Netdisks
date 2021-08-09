package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.impl.LoginServiceImpl;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    private User user;
    private SqlSession session;

    @ResponseBody
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String doLogin(@RequestBody User user){
        LoginServiceImpl service = LoginServiceImpl.getInstance(user);
        return service.getUser().getToken();
    }

}
