package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.entity.User;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    private User user;
    private SqlSession session;
    @ResponseBody
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public User doLogin(@RequestBody User user){
        return user;
    }
}
