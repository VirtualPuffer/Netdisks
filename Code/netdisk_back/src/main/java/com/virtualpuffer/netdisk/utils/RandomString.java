package com.virtualpuffer.netdisk.utils;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class RandomString {
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

    public static int filter(boolean A,boolean a,boolean N){

        return 1;
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
}
