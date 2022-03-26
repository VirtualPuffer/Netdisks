package com.virtualpuffer.netdisk.utils;

import java.awt.*;
import java.awt.font.ShapeGraphicAttribute;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;

public class VerificationCode {

    private static final int weight = 150;
    private static final int height = 60;
    private static final String DefaultFormat = "JPEG";
    private String text;
    private Random r = new Random();
    //private String[] fontNames = {"宋体", "华文楷体", "黑体", "微软雅黑", "楷体_GB2312"};   //字体数组
    //字体数组
    private String[] fontNames = {"Georgia"};
    //验证码数组
    private String codes = "23456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
    private BufferedImage image;
    public VerificationCode(){
        this.image = createImage();
    }

    private Color randomColor() {
        int r = this.r.nextInt(225);
        int g = this.r.nextInt(225);
        int b = this.r.nextInt(225);
        return new Color(r, g, b);
    }

    private Color randomColor(int limit) {
        int r = this.r.nextInt(limit);
        int g = this.r.nextInt(limit);
        int b = this.r.nextInt(limit);
        return new Color(r, g, b);
    }

    private Font randomFont() {
        int index = r.nextInt(fontNames.length);  //获取随机的字体
        String fontName = fontNames[index];
        int style = r.nextInt(4);         //随机获取字体的样式，0是无样式，1是加粗，2是斜体，3是加粗加斜体
        int size = r.nextInt(10) + 40;    //随机获取字体的大小
        return new Font(fontName, style, size);   //返回一个随机的字体
    }

    private char randomChar() {
        int index = r.nextInt(codes.length());
        return codes.charAt(index);
    }

    private void drawLine(BufferedImage image) {
        int num = r.nextInt(5)+5; //定义干扰线的数量
        Graphics2D g = (Graphics2D) image.getGraphics();
        for (int i = 0; i < num; i++) {
            int x1 = r.nextInt(weight);
            int y1 = r.nextInt(height);
            int x2 = r.nextInt(weight);
            int y2 = r.nextInt(height);
            g.setColor(randomColor(125));
            //g.setColor(randomColor());
            //粗线
            g.drawLine(x1, y1, x2, y2);
            g.drawLine(x1, y1+1, x2, y2+1);
            g.drawLine(x1, y1-1, x2, y2-1);
        }
    }

    private void point(BufferedImage image){
        float yawpRate = 0.01f;// 噪声率
        Random random = new Random();
        int area = (int) (yawpRate * weight * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(weight);
            int y = random.nextInt(height);
            image.setRGB(x, y, random.nextInt(255));
        }
    }

    private BufferedImage createImage() {
        BufferedImage image = new BufferedImage(weight, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(new Color(255, 255, r.nextInt(245) + 10));
        g.fillRect(0, 0, weight, height);
        return image;
    }

    public void buildImage() {
        Graphics2D g = (Graphics2D) this.image.getGraphics(); //获取画笔
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++){           //画四个字符即可
            String s = randomChar() + "";      //随机生成字符，因为只有画字符串的方法，没有画字符的方法，所以需要将字符变成字符串再画
            sb.append(s);                      //添加到StringBuilder里面
            float x = i * 1.0F * weight / 4;   //定义字符的x坐标
            g.setFont(randomFont());           //设置字体，随机
            g.setColor(randomColor());         //设置颜色，随机
            g.drawString(s, x+10, height-20);
        }
        this.text = sb.toString();
        drawLine(this.image);
        point(this.image);
    }

    public String getText() {
        return this.text;
    }

    public String getImg(OutputStream outputStream) throws IOException {
        buildImage();
        ImageIO.write(this.image,DefaultFormat,outputStream);
        return getText();
    }
}
