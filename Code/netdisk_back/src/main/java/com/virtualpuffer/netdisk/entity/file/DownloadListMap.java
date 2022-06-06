package com.virtualpuffer.netdisk.entity.file;

import javax.ws.rs.PUT;
import java.util.ArrayList;

public class DownloadListMap{
    public String file_name;
    public boolean straight;
    public int dir_id;
    public ArrayList<Integer> file_list;
    public String tokenTag;
    public int USER_ID;
}
