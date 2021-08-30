package com.virtualpuffer.netdisk.data;

public class Mail{
    String addr;
    String subject;
    String content;

    public Mail() {
    }

    public Mail(String addr, String subject, String content) {
        this.addr = addr;
        this.subject = subject;
        this.content = content;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
