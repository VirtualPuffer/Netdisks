package com.virtualpuffer.netdisk.data;


public class ResponseStatue {
     public boolean state;
     public int code;
     public UserData LogUsers;
     public String msg;


     public ResponseStatue(UserData usd, String msg){
          this.state = true;
          this.code = 200;
          this.LogUsers = usd;
          this.msg = msg;
     }
     public ResponseStatue(String username,String verCode){
          this.state = true;
          this.code = 200;
          this.LogUsers = new UserData(username,verCode);
     }
     public ResponseStatue(){
          this.state = true;
          this.code = 200;
     }
     public ResponseStatue(boolean state,int code){
          //失败逻辑
          this.code = code;
          this.state = false;
          this.LogUsers = null;
     }
     public ResponseStatue(boolean state,int code,String msg){
          //失败逻辑
          this.code = code;
          this.state = false;
          this.LogUsers = null;
          this.msg = msg;
     }

     public boolean isState() {
          return state;
     }

     public int getCode() {
          return code;
     }

     /*public UserData getLogUsers() {
          return LogUsers;
     }*///这个自己有GET方法

     public String getMsg() {
          return msg;
     }
}

