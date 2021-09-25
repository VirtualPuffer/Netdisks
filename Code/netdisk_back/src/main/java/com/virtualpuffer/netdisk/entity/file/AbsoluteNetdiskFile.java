package com.virtualpuffer.netdisk.entity.file;

import com.virtualpuffer.netdisk.entity.BaseEntity;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import com.virtualpuffer.netdisk.mapper.netdiskFile.FileHashMap;
import com.virtualpuffer.netdisk.mapper.netdiskFile.FileMap;
import com.virtualpuffer.netdisk.mapper.user.UserMap;
import com.virtualpuffer.netdisk.service.impl.file.FileUtilService;
import com.virtualpuffer.netdisk.utils.Message;
import com.virtualpuffer.netdisk.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;

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
public class AbsoluteNetdiskFile extends AbsoluteNetdiskEntity{
    private String File_Name;
    private String File_Path;//真实路径
    private int File_Length;
    private String File_Destination_Place;//父级路径
    private String File_Destination;//映射路径（客户真实看到的）
    private String File_Hash;//计算SHA256
    private int userID;//拥有者ID
    private File file;
    private boolean lock = false;

    /**
     * 构造方法有两种：
     * 1.在sql中查询
     * 2.物理路径构造
    * */
    public AbsoluteNetdiskFile(String path){
        String file_Path = StringUtils.filePathDeal(path);
        try {
            this.File_Destination = this.destinationHandle(path);
        } catch (Exception e) {
        }
        this.file = new File(file_Path);
        this.File_Name = file.getName();
        try {
            this.File_Path = this.file.getCanonicalPath();
        } catch (IOException e) {
            this.File_Path = this.file.getAbsolutePath();
        }
    }

    public AbsoluteNetdiskFile(String path, String destination){
        String file_Path = StringUtils.filePathDeal(path);
        this.File_Destination = destination;
        this.file = new File(file_Path);
        this.File_Name = this.file.getName();
        try {
            this.File_Path = this.file.getCanonicalPath();
        } catch (IOException e) {
            this.File_Path = this.file.getAbsolutePath();
        }
    }



    public static AbsoluteNetdiskFile getInstance(String destination, int id) throws FileNotFoundException{
        SqlSession session = null;
        AbsoluteNetdiskFile netdiskFile = null;
        try {
            try {
                netdiskFile = checkMap(destination,id);
                netdiskFile.setFile(new File(netdiskFile.getFile_Path()));
            } catch (FileNotFoundException e) {
                session = MybatisConnect.getSession();
                User user = session.getMapper(UserMap.class).getUserByID(id);
                String path = filePathDeal(getAbsolutePath(destination,user));
                netdiskFile = new AbsoluteNetdiskFile(path,destination);
            }
            if (netdiskFile != null) {
                return netdiskFile;
            } else {
                    throw new FileNotFoundException("路径构建失败2");
                }

        } finally {
            close(session);
        }
    }
    public static AbsoluteNetdiskFile getInstance(String hash, String name) throws FileNotFoundException{
        SqlSession session = null;
        AbsoluteNetdiskFile netdiskFile = null;
        try {
            session = MybatisConnect.getSession();
            String path = session.getMapper(FileHashMap.class).getFilePath(hash).getPath();
            netdiskFile = new AbsoluteNetdiskFile(path);
            if (netdiskFile != null) {
                netdiskFile.setFile_Name(name);
                netdiskFile.setFile_Hash(hash);
                return netdiskFile;
            }else {
                throw new FileNotFoundException("路径构建失败1");
            }
        } finally {
            close(session);
        }
    }

    public static AbsoluteNetdiskFile checkMap(String destination, int id) throws FileNotFoundException{
        SqlSession session = null;
        AbsoluteNetdiskFile netdiskFile = null;
        String file_Destination = StringUtils.filePathDeal(destination);
        try {
            session = MybatisConnect.getSession();
            netdiskFile = session.getMapper(FileMap.class).getFileMap(id,file_Destination);
            if(netdiskFile == null){
                throw new FileNotFoundException();
            }else {
                return netdiskFile;
            }
        } finally {
            session.close();
        }
    }

    public AbsoluteNetdiskFile handleInstance()throws RuntimeException{
        if(this.lock == false){
            this.File_Destination = filePathDeal(this.File_Destination);
            this.File_Path = filePathDeal(this.File_Path);
            this.lock = true;
            return this;
        }else {
            throw new RuntimeException("NetdiskFile has been handle");
        }
    }

    public AbsoluteNetdiskFile handleInstance(String source)throws RuntimeException{
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
    public String destinationHandle(String path){
        StringBuilder builder = new StringBuilder();
        Iterator<String> pathIterator = null;
        Iterator<String> defIterator = null;

        try {
            LinkedList deflist = filePathHandle(defaultWare);
            LinkedList pathlist = filePathHandle(path);
            defIterator = deflist.iterator();
            pathIterator = pathlist.iterator();
            while (defIterator.hasNext() && pathIterator.hasNext()){
                if(!defIterator.next().equals(pathIterator.next())){
                    throw new RuntimeException("unMatch path " + defaultWare + "   " + path);
                }
            }
            pathIterator.next();
        } catch (RuntimeException e) {
            LinkedList deflist = filePathHandle(duplicateFileWare);
            LinkedList pathlist = filePathHandle(path);
            defIterator = deflist.iterator();
            pathIterator = pathlist.iterator();
            while (defIterator.hasNext() && pathIterator.hasNext()){
                if(!defIterator.next().equals(pathIterator.next())){
                    throw new RuntimeException("unMatch path " +
                            duplicateFileWare + "    " + duplicateFileWare + "   " + path);
                }
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


    public AbsoluteNetdiskFile(){}

    public String getFile_Destination_Place() {
        return File_Destination_Place;
    }

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
        File_Destination = StringUtils.filePathDeal(file_Destination);
    }

    public String getFile_Hash() throws Exception {
        if(this.File_Hash == null || this.File_Hash.equals("")){
            this.File_Hash = FileUtilService.getSH256(this.file);
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

    @Override
    public String toString() {
        return "NetdiskFile{" +
                "File_Name='" + File_Name + '\'' +
                ", File_Path='" + File_Path + '\'' +
                ", File_Destination_Place='" + File_Destination_Place + '\'' +
                ", File_Destination='" + File_Destination + '\'' +
                ", File_Hash='" + File_Hash + '\'' +
                ", userID=" + userID +
                ", file=" + file +
                ", lock=" + lock +
                '}';
    }
}
