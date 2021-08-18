package com.virtualpuffer.netdisk.entity;

import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.mapper.FileHashMap;
import com.virtualpuffer.netdisk.mapper.FileMap;
import com.virtualpuffer.netdisk.mapper.UserMap;
import com.virtualpuffer.netdisk.service.impl.BaseServiceImpl;
import com.virtualpuffer.netdisk.service.impl.FileServiceUtil;
import com.virtualpuffer.netdisk.utils.Message;
import com.virtualpuffer.netdisk.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;

import javax.ws.rs.core.Link;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import static com.virtualpuffer.netdisk.utils.StringUtils.filePathDeal;


/**
 * 文件对象
 * 通过path映射或者destination反向映射
 *
 * */
public class NetdiskFile extends BaseServiceImpl implements Serializable {
    private String File_Name;
    private String File_Path;//真实路径
    private String File_Destination;//映射路径（客户真实看到的）
    private String File_Hash;//计算SHA256
    private int userID;//拥有者ID
    private File file;
    private boolean lock = false;
    public static final String downloadAPI = Message.getMess("downloadAPI");//下载链接前缀
    public static final String defaultWare = Message.getMess("defaultWare");
    public static final String duplicateFileWare = Message.getMess("duplicateFileWare");

    /**
     * 构造方法有两种：
     * 1.在sql中查询
     * 2.物理路径构造
    * */
    public NetdiskFile(String path){
        String file_Path = StringUtils.filePathDeal(path);
        this.file = new File(file_Path);
        try {
            this.File_Path = this.file.getCanonicalPath();
        } catch (IOException e) {
            this.File_Path = this.file.getAbsolutePath();
        }
    }


    public static NetdiskFile getInstance(String destination,int id) throws FileNotFoundException{
        SqlSession session = null;
        NetdiskFile netdiskFile = null;
        try {

            try {
                netdiskFile = checkMap(destination,id);
            } catch (FileNotFoundException e) {
                netdiskFile = new NetdiskFile();
                User user = session.getMapper(UserMap.class).getUserByID(id).getFirst();
                netdiskFile.setFile_Path(getAbsolutePath(destination,user));
                netdiskFile.setFile_Destination(destination);
                netdiskFile.setUserID(id);
            }

            if (netdiskFile != null) {
                return netdiskFile;
            }else {
                throw new FileNotFoundException("路径构建失败");
            }

        } finally {
            close(session);
        }
    }
    public static NetdiskFile getInstance(String hash) throws FileNotFoundException{
        SqlSession session = null;
        try {
            session = MybatisConnect.getSession();
            NetdiskFile netdiskFile = session.getMapper(FileHashMap.class).getFileMapByHash(hash);
            if (netdiskFile != null) {
                return netdiskFile;
            }else {
                throw new FileNotFoundException("路径构建失败");
            }
        } finally {
            close(session);
        }
    }

    public static NetdiskFile checkMap(String destination,int id) throws FileNotFoundException{
        SqlSession session = null;
        NetdiskFile netdiskFile = null;
        String file_Destination = StringUtils.filePathDeal(destination);
        try {
            session = MybatisConnect.getSession();
            netdiskFile = session.getMapper(FileMap.class).getFileMap(id,destination);
            if(netdiskFile == null){
                throw new FileNotFoundException();
            }else {
                return netdiskFile;
            }
        } finally {
            session.close();
        }
    }

    public NetdiskFile handleInstance()throws RuntimeException{
        if(this.lock == false){
            this.File_Destination = filePathDeal(this.File_Destination);
            this.lock = true;
            return this;
        }else {
            throw new RuntimeException("NetdiskFile has been handle");
        }
    }

    public NetdiskFile handleInstance(String source)throws RuntimeException{
        if(source == null || source.equals("")){return handleInstance();}
        if(!lock){
            int length = filePathDeal(source).length();
            this.File_Destination = filePathDeal(this.File_Destination.substring(length));
            this.lock = true;
            return this;
        }else {
            throw new RuntimeException("NetdiskFile has been handle");
        }
    }

    public LinkedList filePathHandle(String path){
        String[] dir = path.split("/");
        LinkedList<String> list = new LinkedList<>();
        for(String file : dir){
            if(file.equals("..")){
                list.removeLast();
            }else if(file.equals(".")){

            }else {
                list.add(file);
            }
        }
        return list;
    }

    //将文件数组转换回文件路径
    public void buildPath(String[] dir){
        StringBuilder builder = new StringBuilder();
        for(String file : dir){
            if (file!=null && !file.equals("")) {
                builder.append("/");
                builder.append(file);
            }
        }
    }

    public String buildPath(LinkedList list){
        StringBuilder builder = new StringBuilder();
        Iterator<String> iterator = list.iterator();
        String[] ret = new String[list.size()];
        int index = 0;
        while (iterator.hasNext()){
            String file = iterator.next();
            if (file!=null && !file.equals("")) {
                builder.append("/");
                builder.append(file);
            }
        }
        return builder.toString();
    }
    //将path转化为destination
    public String destinationHandle(String defaultPath,String path){
        StringBuilder builder = new StringBuilder();
        LinkedList deflist = filePathHandle(defaultPath);
        LinkedList pathlist = filePathHandle(path);
        Iterator<String> defIterator = deflist.iterator();
        Iterator<String> pathIterator = pathlist.iterator();
        while (defIterator.hasNext() && pathIterator.hasNext()){
            if(defIterator.next() != pathIterator.next()){
                throw new RuntimeException("unMatch path " + defaultPath + "   " + path);
            }
        }
        while (pathIterator.hasNext()){
            builder.append("/");
            builder.append(pathIterator.next());
        }
        return builder.toString();
    }

    public static String getAbsolutePath(String destination,User user){
        return defaultWare + user.getURL() + destination;
    }


    public NetdiskFile(){}

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFile_Name() {
        return File_Name;
    }

    public void setFile_Name(String file_Name) {
        File_Name = file_Name;
    }

    public String getFile_Path() {
        return File_Path;
    }

    public void setFile_Path(String file_Path) {
        File_Path = file_Path;
    }

    public String getFile_Destination() {
        return File_Destination;
    }

    public void setFile_Destination(String file_Destination) {
        File_Destination = file_Destination;
    }

    public String getFile_Hash() throws Exception {
        if(this.File_Hash == null || this.File_Hash.equals("")){
            this.File_Hash = FileServiceUtil.getSH256(this.file);
        }
        return File_Hash;
    }

    public void setFile_Hash(String file_Hash) {
        File_Hash = file_Hash;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
