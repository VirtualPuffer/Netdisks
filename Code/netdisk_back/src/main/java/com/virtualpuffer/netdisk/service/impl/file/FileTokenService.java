package com.virtualpuffer.netdisk.service.impl.file;

import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskDirectory;
import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskEntity;
import com.virtualpuffer.netdisk.entity.file.AbsoluteNetdiskFile;
import com.virtualpuffer.netdisk.entity.User;
import com.virtualpuffer.netdisk.mapper.netdiskFile.DirectoryMap;
import com.virtualpuffer.netdisk.mapper.netdiskFile.FileMap;
import com.virtualpuffer.netdisk.mapper.user.UserMap;
import com.virtualpuffer.netdisk.service.ParseToken;
import com.virtualpuffer.netdisk.utils.MybatisConnect;
import com.virtualpuffer.netdisk.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.scheduling.annotation.Async;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//@Async
public class FileTokenService extends FileHashService implements ParseToken {
    private Map<Integer,AbsoluteNetdiskDirectory> directoryCache = new HashMap<>();
    private Map tokenMessageMap;
    private HashSet<AbsoluteNetdiskEntity> fileSet;
    private boolean singleFile = false;
    private String packageName ;
    public FileTokenService(AbsoluteNetdiskEntity netdiskEntity, User user,HashSet set,boolean single) throws FileNotFoundException {
        super(netdiskEntity, user);
        this.fileSet = set;
        this.singleFile = single;
    }

    public FileTokenService() throws FileNotFoundException {
    }
    /**
     * 解析下载直链并转存
     * */
    public static FileBaseService getInstanceByURL(String destination, String url, User user) throws FileNotFoundException {
        if(url.substring(0,downloadAPI.length()).equals(downloadAPI)){
            FileBaseService service = getInstanceByToken(url.substring(downloadAPI.length()),null);
            service.setUser(user);
            service.netdiskFile.setFile_Destination(destination + "/" + service.netdiskFile.getFile_Name());
            return service;
        }else {
            throw new RuntimeException("url解析失败");
        }
    }
    /**
     * @param token 需要解析的token
     * 解析token里的FileService对象
     *
     * 没办法确定是hash还是path(不知道是不是文件)
     * 文件夹没办法给hash，只能给路径，被删了就没办法了
     * */
    public static FileTokenService getInstanceByToken(String token, String key) throws FileNotFoundException {
        Map map = null;
        SqlSession session = null;
        AbsoluteNetdiskDirectory netdiskDirectory = null;
        AbsoluteNetdiskFile netdiskFile = null;
        User user = null;
        HashSet<AbsoluteNetdiskEntity> set = new HashSet<>();
        try {
            session = MybatisConnect.getSession();
            map = parseJWT(token,key);
            int userID = (Integer) map.get("userID");
            boolean single = (Boolean) map.get("straight");
            String file_name = (String) map.get("file_name");

            ArrayList<Integer> dirCollection = (ArrayList<Integer>) map.get("dir_id");
            ArrayList<Integer> fileCollection = (ArrayList<Integer>)map.get("file_id");
            user = session.getMapper(UserMap.class).getUserByID(userID);

            for (Integer dir_id : dirCollection) {
                netdiskDirectory = AbsoluteNetdiskDirectory.getInstance(dir_id);
                set.add(netdiskDirectory);
            }
            for(Integer file_id : fileCollection){
                netdiskFile = session.getMapper(FileMap.class).getFileByMapID(file_id);
                set.add(netdiskFile);
            }
            AbsoluteNetdiskEntity instance = netdiskFile == null ? netdiskDirectory : netdiskFile;
            FileTokenService service = new FileTokenService(instance,user,set,single);
            service.tokenMessageMap = map;
            service.setPackageName(file_name);
            return service;
        } finally {
            close(session);
        }
    }

    public long download(OutputStream outputStream) throws IOException {
        if (singleFile != true) {
            File file = new File(tempWare + outputStream.hashCode());
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                ZipOutputStream zipStream = new ZipOutputStream(fileOutputStream);
                compress(zipStream,fileSet,"");
                zipStream.finish();
                long length = file.length();
                FileInputStream inputStream = new FileInputStream(file);
                copy(inputStream,outputStream);
                return length;
            } finally {
                file.delete();
            }
        }else {//单文件模式
            for(AbsoluteNetdiskEntity entity : fileSet){
                AbsoluteNetdiskFile netdiskFile = (AbsoluteNetdiskFile) entity;
                InputStream inputStream = new FileInputStream(netdiskFile.getFile_Path());
                int length = inputStream.available();
                copy(inputStream,outputStream);
                return length;
            }
        }
        throw new RemoteException();
    }

    /**
     * 获取映射文件并放入压缩集合中
     *
     * */
    protected ZipOutputStream compress(ZipOutputStream outputStream, HashSet fileS,String path){
        SqlSession session = null;
        HashSet<AbsoluteNetdiskEntity> fileSet = (HashSet<AbsoluteNetdiskEntity>)fileS;
        for (AbsoluteNetdiskEntity netdiskEntity : fileSet) {
            try {
                if(netdiskEntity instanceof  AbsoluteNetdiskFile){
                    AbsoluteNetdiskFile file = (AbsoluteNetdiskFile)netdiskEntity;
                    String fileName = path + file.getFile_Name();
                    outputStream.putNextEntry(new ZipEntry(fileName));
                    FileInputStream inputStream = new FileInputStream(file.getFile_Path());
                    copy(inputStream,outputStream);
                    close(inputStream);
                    outputStream.closeEntry();
                }else{
                    session = MybatisConnect.getSession();
                    AbsoluteNetdiskDirectory directory = (AbsoluteNetdiskDirectory)netdiskEntity;
                    String nextPath = StringUtils.filePathDeal(path + "/" + directory.getDirectory_Name() + "/");
                    int dir_id = directory.getDirectory_ID();
                    HashSet<AbsoluteNetdiskFile> fileHashSet = session.getMapper(FileMap.class).getChildrenFileID(dir_id);
                    HashSet<AbsoluteNetdiskDirectory> directoryHashSet = session.getMapper(DirectoryMap.class).getChildrenDirID(dir_id);
                    if(fileHashSet.isEmpty() && directoryHashSet.isEmpty()){
                        outputStream.putNextEntry(new ZipEntry(nextPath));
                        outputStream.closeEntry();
                    }else {
                        compress(outputStream,fileHashSet,nextPath);
                        compress(outputStream,directoryHashSet,nextPath);
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                close(session);
            }
        }
        return outputStream;
    }

    public Map getTokenMessageMap() {
        return tokenMessageMap;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
