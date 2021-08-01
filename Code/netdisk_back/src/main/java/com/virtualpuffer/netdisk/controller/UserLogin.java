package com.virtualpuffer.netdisk.controller;

import com.alibaba.fastjson.JSON;
import com.virtualpuffer.netdisk.controller.base.BaseController;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@org.springframework.stereotype.Controller
@RestController
public class UserLogin extends BaseController {
    /*
    * account password username
    * 校验账号名重复性
    * USER_ID数据库自动生成并返回
    * 用USER_ID注册仓库
    * 返回信息
    * */
   /* @Deprecated
    private synchronized static Statement getStatement(){
        return JDBCOBJ.getJDBCOBJpool().getState();
    }*/

    public static int getHash(Object obj){
        return  obj.hashCode() ^ (obj.hashCode() >>> 16);
    }
 /*   *//*
    * 获取单项数据
    * *//*
    @Deprecated
    private static String getBack(String sql,String name){
        try {
            ResultSet res = getStatement().executeQuery(sql);
            res.next();
            return res.getString(name);
        } catch (SQLException throwables) {
            return "";
        }
    }*/

    public static void judgeVerifyNumber(HttpServletRequest request,String verification) throws RuntimeException{
        String value = getCookieValue(request,"verImg");
        String hash = String.valueOf(getHash(verification));
        if(value == hash){
            return ;
        }
        throw new RuntimeException();
    }

    @RequestMapping("/userRegister")
    public static synchronized Object userRegister(String username,String password,String name,HttpServletResponse response,HttpServletRequest request){

        SqlSession session = MybatisConnect.getSession();
        if(username == null || password == null || name == null){
            return JSON.toJSON(new ResponseStatue(false,404,"参数错误或缺少参数"));
        }
       //检测是否重复用户名（账号）
            if(!session.getMapper(UserMap.class).duplicationUsername(username).isEmpty()){
                response.setStatus(300);
                return JSON.toJSON(new ResponseStatue(false,300,"用户名已存在"));
            }

            int first = session.getMapper(UserMap.class).register(username,password,name);
            int second = session.getMapper(UserMap.class).updateURL();
            if(first == 1  &&  second == 1){
                int USER_ID = session.getMapper(UserMap.class).getIDbyName(username);
                if(registerBuild(USER_ID)){//______________________________________________创建仓库
                    response.setStatus(200);
                    //提交
                    session.commit();
                    return JSON.toJSON(new ResponseStatue());
                }else {
                    response.setStatus(500);
                    return JSON.toJSON(new ResponseStatue(false,500,"数据库错误"));
                }
            }
        response.setStatus(500);
        return JSON.toJSON(new ResponseStatue(false,500,"未知错误"));
    }

    @RequestMapping("/userLogin")
    public static Object userLog(String username, String password, String verification,HttpServletResponse response, HttpServletRequest request , boolean freeLogin) throws IOException {
//账号密码
        System.out.println(Log.getTime() + "  Login ->  parameter " + username + " : " + password + " : " + verification + " : ");
        if(username == null || password == null){
            response.setStatus(200);
            ResponseStatue fail = new ResponseStatue(false,300,"username or password is null");
            return JSON.toJSON(fail);
        }
        String verCode = String.valueOf(getHash(((int)System.currentTimeMillis()>>2 + username.hashCode() * password.hashCode())));

//校验码
        try {
            judgeVerifyNumber(request,verification);
        } catch (RuntimeException e) {
            /*response.setStatus(200);
            ResponseStatue fail = new ResponseStatue(false,301,"验证码错误");
            return JSON.toJSON(fail);*/
            System.out.println("验证码爆了");
        }
//------------------------------
        int time = 600;//正常
        if(freeLogin){
            time = 60*60*24*7;//免登录
        }
        //--------------成功逻辑
        if(Verify.verify(username,password,verCode,request.getRemoteAddr(),time)){
            Cookie verC = new Cookie("verCode",verCode);
            verC.setHttpOnly(true);
            verC.setSecure(false);
            verC.setMaxAge(freeLogin ? time : Message.getMessByInt("maxCookAge"));
            response.addCookie(verC);
            ResponseStatue suc = new ResponseStatue(new UserData(verCode),"登陆成功");
            response.setStatus(200);
            return JSON.toJSON(suc);
        }else{
            //-----------------失败逻辑
            ResponseStatue fail = new ResponseStatue(false,300,"username or password error");
            response.setStatus(200);
            return JSON.toJSON(fail);
        }
    }
    @RequestMapping("/userLogout")
    public static Object userLogout(HttpServletResponse response, HttpServletRequest request){
        for(Cookie cookie : request.getCookies()){
            if(cookie.getName().equals("verCode")){
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                //删除成功
                return JSON.toJSON(new ResponseStatue());
            }
        }
        return JSON.toJSON(new ResponseStatue(false,300,"cookie不存在"));
    }

    /*
    * 根据key寻找cookie
    * return value;
    * 找不到直接抛出异常
    *
    * */
    public static String getCookieValue(HttpServletRequest request,String target)throws RuntimeException{
        Cookie[] cookies = request.getCookies();
        if (cookies!=null) {
            for(Cookie cook : cookies){
                if(cook.getName().equals(target)){
                    return cook.getValue();
                }
            }
        }
        throw new RuntimeException("cookie not found");
    }
    //免登录合法性认证

    @RequestMapping("/freeLogin")
    public static Object freelog(HttpServletResponse response, HttpServletRequest request){
        String ip = request.getRemoteAddr();
        String ver = "0";
        //获取cookie
        try {
            ver = getCookieValue(request,"verCode");
        } catch (RuntimeException e) {
            //找不到cookie&没有cookie
            ResponseStatue back = new ResponseStatue(false,300,"fail");
            return JSON.toJSON(back);
        }
        if(Verify.freeLogAdverify(ver,ip) && ver != null){
            ResponseStatue suc = new ResponseStatue(new UserData(ver),"免登录验证成功");
            return JSON.toJSON(suc);
        }

        //状态异常,有verCode但没通过
        ResponseStatue back = new ResponseStatue(false,400,"未知错误");
        return JSON.toJSON(back);
    }

    //验证码接口
    @RequestMapping("/verImg")
    public static void verImg(HttpServletResponse response, HttpServletRequest request){
        try {
            RandomString.buildImg(response);
            response.setStatus(200);
            return;
        } catch (IOException e) {
            response.setStatus(500);
            return;
        }
    }
}
