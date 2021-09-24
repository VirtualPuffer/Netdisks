package com.virtualpuffer.netdisk.service.impl.file;

import com.virtualpuffer.netdisk.utils.Message;

import java.io.*;

public class SpacePictureService extends FileUtilService{
    private int length;
    private String hash;
    private int Picture_id;
    private String File_Path;
    private File picture;
    public static final String spaceBackgroundWare = Message.getMess("spaceBackgroundWare");
    @Override
    public void uploadFile(InputStream input) throws Exception {
    }
    public void downloadFile(OutputStream outputStream)throws Exception{
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(this.picture);
            copy(inputStream,outputStream);
        } finally {
            close(inputStream);
        }
    }
}
