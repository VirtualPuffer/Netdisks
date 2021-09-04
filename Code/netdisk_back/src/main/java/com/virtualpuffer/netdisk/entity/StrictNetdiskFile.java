package com.virtualpuffer.netdisk.entity;

import com.virtualpuffer.netdisk.MybatisConnect;
import com.virtualpuffer.netdisk.mapper.FileMap;
import com.virtualpuffer.netdisk.mapper.UserMap;
import com.virtualpuffer.netdisk.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;

import java.io.File;
import java.io.FileNotFoundException;

import static com.virtualpuffer.netdisk.utils.StringUtils.filePathDeal;

public class StrictNetdiskFile extends AbsoluteNetdiskFile{
    public StrictNetdiskFile(String path) {
        super(path);
    }

    public StrictNetdiskFile(String path, String destination) {
        super(path, destination);
    }

    public static StrictNetdiskFile getInstance(String destination, int id) throws FileNotFoundException {
        SqlSession session = null;
        StrictNetdiskFile netdiskFile = null;
        String file_Destination = StringUtils.filePathDeal(destination);
        try {
          /*  netdiskFile = session.getMapper(FileMap.class).getFileMap(id,file_Destination);*/
            if (netdiskFile != null) {
                return netdiskFile;
            } else {
                throw new FileNotFoundException("路径构建失败2");
            }

        } finally {
            close(session);
        }
    }

}
