package com.virtualpuffer.netdisk.entity.file;

import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.netdiskFile.DirectoryMap;
import com.virtualpuffer.netdisk.mapper.netdiskFile.FileMap;
import com.virtualpuffer.netdisk.mapper.user.UserMap;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import org.apache.ibatis.session.SqlSession;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.LinkedList;

import static com.virtualpuffer.netdisk.utils.StringUtils.filePathDeal;
import static com.virtualpuffer.netdisk.utils.StringUtils.getFileSequence;


/**
 * 用链表的形式实现抽象文件系统
 * 每个子文件保存父文件的id
 * 定位的时候通过id寻找
 *
 * 每个用户的顶级路径是‘/’
 * 父节点id是-1（父节点本身是不存在的）
 * 通过父节点寻址
 *
 * 获取对象时通过路径名一层一层往下定位(userid,dir_id,dir_name)
 * 除了默认的头节点是-1，其他的id是唯一的
 * */
public class AbsoluteNetdiskDirectory extends AbsoluteNetdiskEntity{
    private int USER_ID;
    private int Directory_ID;
    private int Directory_Parent_ID;
    private String Directory_Name;
    LinkedList<AbsoluteNetdiskFile> fileList;
    public static final int HEAD_NODE_ID = -1;

    public AbsoluteNetdiskDirectory(){

    }

    public static AbsoluteNetdiskDirectory getInstance(String destination, int id) throws FileNotFoundException {
        SqlSession session = null;
        AbsoluteNetdiskDirectory netdiskDirectory = null;
        try {
            session = MybatisConnect.getSession();
            String[] fileSequence = getFileSequence(destination);
            int parentID = HEAD_NODE_ID;//头节点位置
            for(String file : fileSequence){
                netdiskDirectory = session.getMapper(DirectoryMap.class).getChildrenDirectory(id,parentID,file);
                if(netdiskDirectory == null){
                    throw new FileNotFoundException("文件夹不存在: 问题路径->  " + file);
                }else {
                    //更新父级ID
                    parentID = netdiskDirectory.getDirectory_ID();
                }
            }
            return netdiskDirectory;
        } finally {
            close(session);
        }
    }
    public void getDir(){
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            LinkedList dirList = session.getMapper(DirectoryMap.class).getDir(this.USER_ID,Directory_ID);
            LinkedList fileList = session.getMapper(FileMap.class).getDir(this.USER_ID,this.Directory_ID);
        } finally {
            close(session);
        }
    }

    public void mkdir(String name){
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            LinkedList exit = session.getMapper(DirectoryMap.class).onExists(USER_ID,Directory_ID,name);
            if(exit.isEmpty()){
                session.getMapper(DirectoryMap.class).mkdir(this.USER_ID,name,this.Directory_Parent_ID);
                session.commit();
            }else {
                throw new RuntimeException("同名文件夹已经存在");
            }
        } finally {
            close(session);
        }
    }

    public int getDirectory_ID() {
        return Directory_ID;
    }

    public void setDirectory_ID(int directory_ID) {
        Directory_ID = directory_ID;
    }

    public int getDirectory_Parent_ID() {
        return Directory_Parent_ID;
    }

    public void setDirectory_Parent_ID(int directory_Parent_ID) {
        Directory_Parent_ID = directory_Parent_ID;
    }

    public String getDirectory_Name() {
        return Directory_Name;
    }

    public void setDirectory_Name(String directory_Name) {
        Directory_Name = directory_Name;
    }
}
