package com.virtualpuffer.netdisk.entity.file;

import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.utils.Message;

import java.io.Serializable;

public abstract class AbsoluteNetdiskEntity extends BaseEntity implements Serializable {
    public static final String downloadAPI = Message.getMess("downloadAPI");//下载链接前缀
    public static final String defaultWare = Message.getMess("defaultWare");
    public static final String duplicateFileWare = Message.getMess("duplicateFileWare");

    public abstract void rename(String name);
}
