package com.virtualpuffer.netdisk.service.messageService;

import com.sun.mail.util.MailSSLSocketFactory;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.Properties;

public class SendMail extends BaseServiceImpl implements Runnable{
    public Thread thread;
    public Session session;
    private Transport transport;
    private boolean runnable = true;
    private static volatile SendMail sendMail;
    private static LinkedList<MimeMessage> list = new LinkedList<>();
    private String host = "smtp.qq.com";
    private static final String From = "547798198@qq.com";
    private static final String Recipient = "547798198@qq.com";
    private static final String Password = "qykmsmflodptbeea";

    public SendMail(){}

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
        list.add(get);
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
                            return new PasswordAuthentication(Recipient,Password);
                }
            });

            while (runnable) {
                try {
                    if(!list.isEmpty()){
                        getConnect();
                        synchronized (SendMail.class) {
                            MimeMessage mimeMessage = list.getFirst();
                            transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
                            list.removeFirst();
                        }
                        Thread.sleep(9000);
                    }
                    Thread.sleep(1000);
                } catch (MessagingException e) {
                    errorLog.errorLog(e.getMessage());
                } catch (InterruptedException e) {
                    errorLog.errorLog(e.getMessage());
                }
            }
            System.out.println("邮件线程关闭");
            transport.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getConnect() throws InterruptedException {
        if(transport == null || !transport.isConnected()){
            try {
                transport = session.getTransport();
                transport.connect(host,From,Password);
                Thread.sleep(1000);
                System.out.println("获取链接:" + transport);
            } catch (NoSuchProviderException e) {
                Thread.sleep(10000);
                System.out.println("fail getting connect");
                errorLog.systemLog(e.getMessage());
            } catch (MessagingException e) {
                Thread.sleep(10000);
                errorLog.systemLog(e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

