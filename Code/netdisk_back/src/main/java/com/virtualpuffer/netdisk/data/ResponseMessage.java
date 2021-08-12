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

}

