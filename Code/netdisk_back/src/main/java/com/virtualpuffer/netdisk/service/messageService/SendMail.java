package com.virtualpuffer.netdisk.service.messageService;

import com.sun.mail.util.MailSSLSocketFactory;
import com.virtualpuffer.netdisk.entity.User;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Properties;

public class SendMail extends Thread {
    private String From = "547798198@qq.com";
    private String recipient = "547798198@qq.com";
    private String password = "oapzqymzhspubedj";
    //邮件发送的服务器
    private String host = "smtp.qq.com";

    //收件人信息
    private User user;
    public SendMail(User user){
        this.user = user;
    }

    @Override
    public void run() {
        try {
            Properties properties = new Properties();
            properties.setProperty("mail.host","smtp.qq.com");

            properties.setProperty("mail.transport.protocol","smtp");
            properties.setProperty("mail.smtp.auth","true");

            //QQ存在一个特性设置SSL加密
            MailSSLSocketFactory sf = null;
            try {
                sf = new MailSSLSocketFactory();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);

            //创建一个session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(recipient,password);
                }
            });
            session.setDebug(false);

            //获取连接对象
            Transport transport = null;
            try {
                transport = session.getTransport();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }

            System.out.println(1);

            //连接服务器
            transport.connect(host,From,password);


            MimeMessage mimeMessage = new MimeMessage(session);
            //邮件发送人
            mimeMessage.setFrom(new InternetAddress(recipient));
            //邮件接收人
            mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress("3230343500@qq.com"));
            //邮件标题
            mimeMessage.setSubject("网站注册成功");
            //邮件内容
            mimeMessage.setContent("网站注册成功，密码为"+user.getPassword()+"，请妥善保管密码","text/html;charset=UTF-8");
            //发送邮件
            transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());

            transport.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}

