package com.virtualpuffer.netdisk.utils;


import com.virtualpuffer.netdisk.DemoFactory;
import com.virtualpuffer.netdisk.Singleton;

import java.sql.*;
import java.util.LinkedList;


/*
 * 换成mybatis,这东西已经可以歇菜了
 *
 * */
@Deprecated
@Singleton
public class JDBCOBJ {
    /*
     * 池子是活的
     * connection至少应该活一个
     * statement用就给，用完就放掉
     * 下面的守护线程负责干掉用完的statement
     * */
    private static final JDBCOBJ JDBCOBJpool = new JDBCOBJ();

    private JDBCOBJ(){
        /*
         * 启动守护线程
         * 获取连接
         * */
        /*Thread demo = new Thread(new cleanDemo());
        demo.setDaemon(true);
        demo.start();*/
        DemoFactory.getDemoFactory().buildDemo(cleanDemo.class);
        getConnect();
    };

    private Connection con = null;
    private Statement state = null;
    private boolean statu = true;
    private LinkedList<statePack> stateList = new LinkedList<statePack>();

    public static JDBCOBJ getJDBCOBJpool(){
        return JDBCOBJpool;
    }

    public LinkedList<statePack> getStateList() {
        return stateList;
    }

    /*
     * 获取state
     * */
    public synchronized Statement getState() {
        Statement out = null;
        try {
            if(con.isClosed() || con ==null){
                getConnect();
            }
            out = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            statePack pack = new statePack(out);
            stateList.add(pack);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return out;
    }

    /*
     * 清理过期线程
     * */
    public static void clean(){
        try {
            LinkedList<statePack> statePack = getJDBCOBJpool().getStateList();
            if(!statePack.isEmpty()){
                if (statePack.getFirst().getTime() < System.currentTimeMillis() && !statePack.getFirst().getStatement().isClosed()){
                    statePack.getFirst().getStatement().close();
                    statePack.removeFirst();
                    //System.out.println("清理");
                    clean();
                }
            }
            return;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return;
    }

    private void getConnect() {
        String sqlUsername = Message.getMess("sqlusername");
        String sqlPassword = Message.getMess("sqlpassword");
        String sqlURL = Message.getMess("sqlURL");
        try {
        /*    Class.forName("com.mysql.jdbc.Driver");*/
            con = DriverManager.getConnection(sqlURL,sqlUsername,sqlPassword);
            state = con.createStatement();
        } /*catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/ catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

}

class statePack{
    private Statement statement;
    private long time;

    public long getTime() {
        return time;
    }

    public statePack(Statement statement) {
        this.statement = statement;
        this.time = System.currentTimeMillis() + 6000;
    }
    public statePack(Statement statement, long time) {
        this.statement = statement;
        this.time = System.currentTimeMillis() + 1000 * time;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }
}

class cleanDemo implements Runnable{
    private  boolean Runnable = true;
    @Override
    public void run() {
        while(Runnable){
            JDBCOBJ.clean();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
