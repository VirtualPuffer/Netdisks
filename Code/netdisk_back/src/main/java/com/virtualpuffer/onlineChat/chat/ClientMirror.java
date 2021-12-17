package com.virtualpuffer.onlineChat.chat;

import java.io.InputStream;
import java.io.OutputStream;

public class ClientMirror {
    public InputStream inputStream;
    public OutputStream outputStream;
    public int code;

    public ClientMirror(InputStream inputStream, OutputStream outputStream, int code) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.code = code;
    }
}
