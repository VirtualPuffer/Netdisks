package com.virtualpuffer.netdisk.controller;

import com.virtualpuffer.netdisk.utils.JDBCOBJ;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;


@org.springframework.stereotype.Controller
@RestController
public class ShowData {

    static LinkedList<String> test;
    static HashMap<String,LinkedList<String>> hasf;

    @RequestMapping("/")
    public static String a(){
        System.out.println(1);
        return "HelloWorld";
    }

    @RequestMapping("/api/background")
    public synchronized StringBuffer handlf(String username, String password, HttpServletResponse response, HttpServletRequest request){
        String json = "123";
        StringBuffer strhead  = new StringBuffer();
        StringBuffer strend = new StringBuffer();
        strhead.append("<head> "+ '\n' +"<body>"+ '\n' + '\n');
        td("user",strhead,"white");
        td("login_verify",strhead);
        td("login_history",strhead);
        td("FileHash_Map",strhead);
            td("File_Map",strhead);
        strend = new StringBuffer();


        // strend.append("</tr></table>");
        strend.append("</body></head>");
        strhead.append(strend);
 /*       System.out.println(strhead);
        System.out.println(json.toString());*/
        response.setHeader("Custom-Header", "foo");
        response.setStatus(200);

        return strhead ;
    }
    /*
     * 横板打印
     * ————————————————————
     * 列名    |   内容
     * 列名    |   内容
     * 列名    |   内容
     * —————————————————————
     * */

    public static void tr(String sqlName,StringBuffer strhead){
        try {
            String sql = "select * from " + sqlName;
            StringBuffer strend = new StringBuffer();
            Statement state = JDBCOBJ.getJDBCOBJpool().getState();
            ResultSet forColumname = state.executeQuery(sql);
            ResultSetMetaData rem = forColumname.getMetaData();
            strend.append("<tr><table width=40 border=\"1\">"+ '\n');
            forColumname.last();
            strend.append( "<td align=\"center\" " + "colspan=\"" + forColumname.getRow() + 1 + "\"" + ">"+ rem.getTableName(1) + "</td>" + '\n');
            strhead.append(strend);
            int count = rem.getColumnCount();
            int index = 1;
            while (index < count+1) {

                ResultSet forData = state.executeQuery(sql);
                strend = new StringBuffer();
                strend.append("<tr>" + '\n');
                String columname = rem.getColumnName(index);
                strend.append( "<td>"+ columname + "</td>" + '\n');
                while (forData.next()){
                    //列生成
                    String ver = forData.getString(columname);
                    strend.append( "<td>"+ ver  + "</td>" + '\n');
                }

                strend.append("</tr>" + '\n');//尾部附加
                strhead.append(strend);
                index++;//指针右移
            }
            strend = new StringBuffer();
            strend.append("</tr></table>");
            strend.append("<br>");
            strhead.append(strend);
            // json = JSON.toJSONString(Vercode);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    /*
     *纵向打印
     *
     * ————————————————————————————————
     *               表名
     * ————————————————————————————————
     *  | 列名    |   列名  |   列名  |
     * ————————————————————————————————
     *  | 内容    |   内容  |   内容  |
     * ————————————————————————————————
     *  | 内容    |   内容  |   内容  |
     *
     *
     * hashmap结构:(列名-K,链表-V)
     * 以链表储存对应列的数据,
     * 链表起始位0，空表起始位-1
     * 0位为列名
     * 1之后是数据
     * */
    public static void ts(String sqlName, LinkedList<String> columnName , HashMap<String,LinkedList<String>> map, StringBuffer tableName){
        try {
            String sql = "select * from " + sqlName;
            Statement state = JDBCOBJ.getJDBCOBJpool().getState();
            ResultSet forColumname = state.executeQuery(sql);
            ResultSetMetaData rem = forColumname.getMetaData();
            tableName.append(rem.getTableName(1));
            forColumname.last();
            int count = rem.getColumnCount();
            int index = 1;
            while (index < count+1) {
                state = JDBCOBJ.getJDBCOBJpool().getState();
                ResultSet forData = state.executeQuery(sql);
                rem = forData.getMetaData();
                String columname = rem.getColumnName(index);
                columnName.add(columname);
                map.put(columname,new LinkedList<String>());
                map.get(columname).add(columname);
                while (forData.next()){
                    //列生成
                    String ver = forData.getString(columname);
                    //按列获取数据
                    map.get(columname).add(ver);
                }
                index++;//指针右移
            }
            // json = JSON.toJSONString(Vercode);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public static void td(String sqlName,StringBuffer strhead){

        HashMap<String,LinkedList<String>> has = new HashMap<String,LinkedList<String>>();
        LinkedList<String> columnName = new LinkedList<String>();
        hasf = has;
        test = columnName;
        StringBuffer tableName = new StringBuffer();
        ts(sqlName,columnName,has,tableName);
        //调用TS处理

        StringBuffer strend = new StringBuffer();
        strend.append("<tr><table width=40 border=\"1\"><tbody>"+ '\n');
        int count = columnName.lastIndexOf(columnName.getLast());
        strend.append( "<tr><td align=\"center\" " + "colspan=\"" + count + 1 + "\"" + ">"+ tableName + "</td></tr>" + '\n');
        //表头

        /*
        //旧版表名附加
        int columnIndex = 0;
        while (columnIndex < columnName.lastIndexOf(columnName.getLast()) + 1) {
            String columname = columnName.get(columnIndex);//-------------------
            strend.append( "<td>"+ columname + "</td>" + '\n');
            columnIndex++;
        }*/
        strend.append("</tr>" + '\n');
        strhead.append(strend);
        //附加表结构

        int index = 0;
        int hasIndex = 0;

        /*
         *try防止空表（链表空的时候长度会返回-1，一调马上爆炸）
         * */
        try {
            hasIndex = has.get(columnName.get(index)).lastIndexOf(has.get(columnName.get(index)).getLast());
        } catch (NoSuchElementException e) {
            hasIndex = -1;
        }

        while (index < hasIndex + 1) {
            strend = new StringBuffer();
            strend.append("<tr>" + '\n');
            int columcount = 0;
            while (columcount < columnName.lastIndexOf(columnName.getLast()) + 1 ){
                //列生成
                String ver = has.get(columnName.get(columcount)).get(index);//----------------------
                strend.append( "<td nowrap=\"nowrap\">"+ ver  + "</td>" + '\n');
                columcount ++;
            }

            strend.append("</tr>" + '\n');//尾部附加
            index++;//指针右移
            strhead.append(strend);
        }
        strend = new StringBuffer();
        strend.append("</tr></tbody></table>");
        strend.append("<br>");
        strhead.append(strend);
        // json = JSON.toJSONString(Vercode);
    }

    public static void td(String sqlName,StringBuffer strhead,String bgcolor){

        HashMap<String,LinkedList<String>> has = new HashMap<String,LinkedList<String>>();
        LinkedList<String> columnName = new LinkedList<String>();
        hasf = has;
        test = columnName;
        StringBuffer tableName = new StringBuffer();
        ts(sqlName,columnName,has,tableName);
        //调用TS处理

        StringBuffer strend = new StringBuffer();
        strend.append("<tr><table width=40 border=\"1\" bgcolor=\"" + bgcolor + "\"><tbody>"+ '\n');
        int count = columnName.lastIndexOf(columnName.getLast());
        strend.append( "<tr><td align=\"center\" " + "colspan=\"" + count + 1 + "\"" + ">"+ tableName + "</td></tr>" + '\n');
        //表头

        /*
        //旧版表名附加
        int columnIndex = 0;
        while (columnIndex < columnName.lastIndexOf(columnName.getLast()) + 1) {
            String columname = columnName.get(columnIndex);//-------------------
            strend.append( "<td>"+ columname + "</td>" + '\n');
            columnIndex++;
        }*/
        strend.append("</tr>" + '\n');
        strhead.append(strend);
        //附加表结构

        int index = 0;
        int hasIndex = 0;

        /*
         *try防止空表（链表空的时候长度会返回-1，一调马上爆炸）
         * */
        try {
            hasIndex = has.get(columnName.get(index)).lastIndexOf(has.get(columnName.get(index)).getLast());
        } catch (NoSuchElementException e) {
            hasIndex = -1;
        }

        while (index < hasIndex + 1) {
            strend = new StringBuffer();
            strend.append("<tr>" + '\n');
            int columcount = 0;
            while (columcount < columnName.lastIndexOf(columnName.getLast()) + 1 ){
                //列生成
                String ver = has.get(columnName.get(columcount)).get(index);//----------------------
                strend.append( "<td nowrap=\"nowrap\">"+ ver  + "</td>" + '\n');
                columcount ++;
            }

            strend.append("</tr>" + '\n');//尾部附加
            index++;//指针右移
            strhead.append(strend);
        }
        strend = new StringBuffer();
        strend.append("</tr></tbody></table>");
        strend.append("<br>");
        strhead.append(strend);
        // json = JSON.toJSONString(Vercode);
    }
}
