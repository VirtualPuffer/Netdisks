package com.virtualpuffer.netdisk.data;


import java.io.Serializable;
import java.util.Map;

public class ResponseMessage implements Serializable {
     public int code;
     public String state;
     public String msg;
     public Map data;

     public ResponseMessage(int code, String state, String msg, Map map) {
          this.code = code;
          this.state = state;
          this.msg = msg;
          this.data = map;
     }

     public ResponseMessage() {
     }
     public static ResponseMessage getSuccessInstance(int code,String message,Map data){
          return new ResponseMessage(code,"success",message,data);
     }

     public static ResponseMessage getExceptionInstance(int code,String message,Map data){
          return new ResponseMessage(code,"exception",message,data);
     }

     public static ResponseMessage getErrorInstance(int code,String message,Map data){
          return new ResponseMessage(code,"error",message,data);
     }

     public int getCode() {
          return code;
     }

     public void setCode(int code) {
          this.code = code;
     }

     public String getState() {
          return state;
     }

     public void setState(String state) {
          this.state = state;
     }

     public String getMsg() {
          return msg;
     }

     public void setMsg(String msg) {
          this.msg = msg;
     }

     public Map getData() {
          return data;
     }

     public void setData(Map data) {
          this.data = data;
     }
}

