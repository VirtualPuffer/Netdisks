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
    private static String From = "547798198@qq.com";
    private static String recipient = "547798198@qq.com";
    private String password = "qykmsmflodptbeea";
    private LinkedList<MimeMessage> list = new LinkedList<>();
    private String host = "smtp.qq.com";
    private boolean runnable = true;
    private static volatile SendMail sendMail;
    public Thread thread;
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

    public static PortMessage buildMessage(String addr,String subject,String content) throws MessagingException {
        MimeMessage mimeMessage = new PortMessage(SendMail.getInstance().session);
        mimeMessage.setFrom(new InternetAddress(From));
        mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress(addr));
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(content,"text/html;charset=UTF-8");
        return (PortMessage)mimeMessage;
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


            while (transport == null || !transport.isConnected()){
                try {
                    transport = session.getTransport();
                    transport.connect(host,From,password);
                   Thread.sleep(1000);
                    System.out.println(transport);
                } catch (NoSuchProviderException e) {
                    System.out.println("fail getting connect");
                    System.out.println(e.getMessage());
                }
            }

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
            transport.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }
}

