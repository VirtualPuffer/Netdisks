package com.virtualpuffer.netdisk.service.messageService;

import com.sun.mail.util.MailSSLSocketFactory;
import com.virtualpuffer.netdisk.DemoFactory;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.Properties;

public class SendMail extends BaseServiceImpl implements Runnable{
    private String From = "547798198@qq.com";
    private String recipient = "547798198@qq.com";
    private String password = "qykmsmflodptbeea";
    private LinkedList<MimeMessage> list = new LinkedList<>();
    private String host = "smtp.qq.com";
    private boolean runnable = true;
    private static volatile SendMail sendMail;
    public Session session;

    private SendMail(){}

    public static SendMail getInstance(){
        if(sendMail == null){//1
            synchronized (SendMail.class){//2
                if(sendMail == null){//3
                    sendMail = new SendMail();//4
                }
            }
        }
        return sendMail;
    }

    public static void sendEmail(MimeMessage get){
        getInstance().list.add(get);
    }

    @Override
    public void run() {
        Transport transport = null;
        try {
            Properties properties = new Properties();
            properties.setProperty("mail.host","smtp.qq.com");
            properties.setProperty("mail.transport.protocol","smtp");
            properties.setProperty("mail.smtp.auth","true");
            MailSSLSocketFactory sf = null;
            try {
                sf = new MailSSLSocketFactory();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(recipient,password);
                }
            });
            session.setDebug(false);

            try {
                transport = session.getTransport();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
            transport.connect(host,From,password);

            while (runnable) {
                if(!list.isEmpty()){
                    try {
                        MimeMessage mimeMessage = list.getFirst();
                        transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
                        list.removeFirst();
                        Thread.sleep(10000);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    } catch (InterruptedException i){}
                }
            }
/*
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
*/
            transport.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

