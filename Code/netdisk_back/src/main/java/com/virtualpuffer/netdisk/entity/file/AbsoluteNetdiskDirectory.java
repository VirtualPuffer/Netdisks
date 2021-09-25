package com.virtualpuffer.netdisk.entity.file;

import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.netdiskFile.DirectoryMap;
import com.virtualpuffer.netdisk.mapper.user.UserMap;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import org.apache.ibatis.session.SqlSession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.LinkedList;

import static com.virtualpuffer.netdisk.utils.StringUtils.filePathDeal;


/**
 * 用链表的形式实现抽象文件系统
 * 每个子文件保存父文件的id
 * 定位的时候通过id寻找
 *
 * */
public class AbsoluteNetdiskDirectory extends AbsoluteNetdiskEntity{
    User user;
    File directory;
    String directoryName;
    String directory_Destination;
    LinkedList<AbsoluteNetdiskFile> fileList;

    public AbsoluteNetdiskDirectory(){

    }

    public static AbsoluteNetdiskDirectory getInstance(String destination, int id) throws FileNotFoundException {
        SqlSession session = null;
        AbsoluteNetdiskDirectory netdiskDirectory = null;
        try {
            session = MybatisConnect.getSession();
            User user = session.getMapper(UserMap.class).getUserByID(id);
            netdiskDirectory = session.getMapper(DirectoryMap.class).getDirectory(id,destination);
            if (netdiskDirectory != null) {
                return netdiskDirectory;
            } else {
                throw new FileNotFoundException("路径构建失败2");
            }

        } finally {
            close(session);
        }
    }
    public void getDir(){

    }
}
