package com.virtualpuffer.netdisk.service.messageService;

import com.sun.mail.util.MailSSLSocketFactory;
import com.virtualpuffer.netdisk.data.Mail;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.Properties;

public class SendMail extends BaseServiceImpl implements Runnable{
    private String host;
    private String from;
    private String recipient;
    private String password;
    public Thread thread;
    private Session session;
    private Transport transport;
    private boolean runnable = true;
    //private static volatile SendMail sendMail;
    private static boolean load = false;
    private static LinkedList<Mail> list = new LinkedList<>();
    public static final String QQ_HOST = "smtp.qq.com";
    public static final String M163_HOST = "smtp.163.com";

    public static void build(SendMail sendMail){
        Thread thread = new Thread(sendMail);
        thread.start();
    }

    public static void load(){
        build(new SendMail("zhongyale797@163.com","zhongyale797@163.com","YAWIOJZRTINOIUFG",SendMail.M163_HOST));
        build(new SendMail("547798198@qq.com","547798198@qq.com","qykmsmflodptbeea",SendMail.QQ_HOST));
        build(new SendMail("1415751897@qq.com","1415751897@qq.com","erthemzwgmbngcbh",SendMail.QQ_HOST));
    }

    public static void sendEmail(Mail get){
        if(!load){
            load();
        }
        list.add(get);
    }

    public SendMail(String from,String recipient,String password,String host){

        this.from  =from;
        this.recipient = recipient;
        this.password = password;
        this.host = host;
        Properties properties = new Properties();
        properties.setProperty("mail.host",host);
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
    }

    public PortMessage buildMessage(Mail mail) throws MessagingException {
        MimeMessage mimeMessage = new PortMessage(session);
        mimeMessage.setFrom(new InternetAddress(this.from));
        mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress(mail.getAddr()));
        mimeMessage.setSubject(mail.getSubject());
        mimeMessage.setContent(mail.getContent(),"text/html;charset=UTF-8");
        return (PortMessage)mimeMessage;
    }

    @Override
    public void run() {
        try {
            while (runnable) {
                try {
                    synchronized (SendMail.class) {
                        if(!list.isEmpty()){
                            getConnect();
                            MimeMessage mimeMessage = buildMessage(list.getFirst());
                            transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
                            list.removeFirst();
                            Thread.sleep(9000);
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    log.errorLog(e.getMessage());
                }
            }
            transport.close();
        }catch (Exception e){
            log.errorLog(e.getMessage());
            e.printStackTrace();
        }
    }
    public void getConnect() throws InterruptedException {
        if(transport == null || !transport.isConnected()){
            try {
                transport = session.getTransport();
                transport.connect(host,from,password);
                Thread.sleep(1000);
                System.out.println("获取链接:" + transport);
            } catch (NoSuchProviderException e) {
                Thread.sleep(10000);
                System.out.println("fail getting connect");
                log.systemLog(e.getMessage());
            } catch (MessagingException e) {
                Thread.sleep(10000);
                log.systemLog(e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


