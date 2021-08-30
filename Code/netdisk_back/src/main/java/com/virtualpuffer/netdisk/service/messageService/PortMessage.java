package com.virtualpuffer.netdisk.service.messageService;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;

public class PortMessage extends MimeMessage {

    public PortMessage(Session session) {
        super(session);
    }

    public PortMessage(Session session, InputStream is) throws MessagingException {
        super(session, is);
    }

    public PortMessage(MimeMessage source) throws MessagingException {
        super(source);
    }

    protected PortMessage(Folder folder, int msgnum) {
        super(folder, msgnum);
    }

    protected PortMessage(Folder folder, InputStream is, int msgnum) throws MessagingException {
        super(folder, is, msgnum);
    }

    protected PortMessage(Folder folder, InternetHeaders headers, byte[] content, int msgnum) throws MessagingException {
        super(folder, headers, content, msgnum);
    }
}
