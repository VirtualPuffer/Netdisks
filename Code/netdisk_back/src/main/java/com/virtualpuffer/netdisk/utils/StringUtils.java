package com.virtualpuffer.netdisk.utils;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    static final int A_Z_MIN = 65;
    static final int a_z_MIN = 97;
    static final int A_Z_MAX = 90;
    static final int a_z_MAX = 122;
    static final int NUM_MIN = 48;
    static final int NUM_MAX = 57;

    //随机范围：48-122
    public static int getRan(){
        Random a = new Random();
        int ret = a.nextInt(122);
        if(ret < NUM_MIN){
            return getRan();
        }else {
            return ret;
        }
        /*int ret = a.nextInt(1220);
        if(ret < NUM_MIN+500){
            return getRan();
        }else {
            return ret;
        }*/
    }

    /*
    * A:大写
    * a：小写
    * N：数字
    * */
    public static int getHash(Object obj){
        return  obj.hashCode() ^ (obj.hashCode() >>> 16);
    }

    public static String filePathDeal(String s ){
        if(!s.startsWith("/")){
            s = new StringBuilder().append("/").append(s).toString();
        }
        if(s.length() <= 1){return s;}
        return filePathDeal(new StringBuilder(),s,0);
    }

    //这个有Bug,/..没处理
    @Deprecated
    public static String filePathDeal(StringBuilder sb,String s ,int index){
        Queue queue = new PriorityQueue();
        while(s.length() > index + 1){
            if(s.charAt(index) == '/' && (s.charAt(index+1) == '/')){
                index = index + 1;
            }
            else if(s.charAt(index) == '/' && (s.charAt(index+1) == '.' && (s.charAt(index+2)) == '.')){
                index = index + 3;
                System.out.println(sb.toString());
                sb.delete(sb.substring(0, sb.lastIndexOf("/")).length(),sb.length());
            }
            else if(s.charAt(index) == '/' && (s.charAt(index+1) == '.')){
                index = index + 2;
            }else {
                sb.append(s.charAt(index));
                index ++;
            }
        }
        sb.append(s.charAt(index++));
      /*  sb.append(s.charAt(index++));*/
        return sb.toString();
    }

    //文件名重复时加上个1
    public static String duplicateRename(String path){
        int index = path.lastIndexOf(".");
        String fir = null;
        String sec = null;
        try {
            fir = path.substring(0, index);
            sec = path.substring(index);
        } catch (Exception e) {
        }
        if(index == -1){
            try {
                return autoUp(path,"");
            } catch (Exception e) {
                return path + "(1)";
            }
        }else {
            try {
                return autoUp(fir,sec);
            } catch (Exception e) {
                return fir + "(1)" + sec;
            }
        }
    }
    public static String autoUp(String fir,String sec){
        if(fir.endsWith(")")){
            try {
                String num = fir.substring(fir.lastIndexOf("(") + 1,fir.length()-1);
                int get = Integer.parseInt(num) + 1;
                return new StringBuilder().append(fir.substring(0,fir.lastIndexOf('(')+1)).append(get).append(')').append(sec).toString();
          /*      return fir.substring(0,fir.lastIndexOf("(")+1) + get + ")" + sec;*/
            } catch (Exception e) {
                e.printStackTrace();
                return new StringBuilder().append(fir).append("(1)").append(sec).toString() ;
            }
        }
        throw new RuntimeException("");
    }



    public static int filter(boolean A,boolean a,boolean N){

        return 1;
    }

    public static String StringFilterUtil(String get){
        String ill = "[ !@#$%^&*()_+-={};':,./<>?！@#￥%……&*（）]";
        Pattern pattern = Pattern.compile(ill);
        Matcher matcher = pattern.matcher(get);
        return matcher.replaceAll("").trim();
    }

    public static String ranStr(int num){
        StringBuffer ret = new StringBuffer();
        int init = 0;
        while (init < num){
            int get = getRan();
            if( (get<= A_Z_MAX && get >= A_Z_MIN) || (get >= NUM_MIN && get <= NUM_MAX) ){
                ret.append((char)get);
                init++;
            }
        }
        return ret.toString();
    }


    //图片生成
    public static String buildImg(HttpServletResponse response) throws IOException {
        int width = 80;
        int height = 30;
        String verNum = ranStr(5);
        int retNum = getHash(verNum);
        String ret = String.valueOf(retNum);

        Cookie cookie = new Cookie("verImg",ret);
        cookie.setMaxAge(-1);
        response.addCookie(cookie);

        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.yellow);
        g.fillRect(0,0,width,height);
        g.setFont(new Font(null,Font.BOLD,20));
        g.setColor(Color.blue);
        g.drawString(verNum,0,20);
        response.setContentType("image/png");//返回类型
        response.setHeader("expires","-1");//响应头
        //hashcode作为文件名
        response.addHeader("Content-Disposition", "fileName=" + ret);
        ImageIO.write(image,"png",response.getOutputStream());
        return ret;
    }
    public static boolean isEmpty(String in){
        if(in.equals("") || in == null){
            return true;
        }else {
            return false;
        }
    }
}
