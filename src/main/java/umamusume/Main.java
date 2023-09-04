package umamusume;

import entity.UserInfo;
import utils.OrderExecute;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import static utils.Hikari.getConnection;


public class Main{
    public static String appVer = "1.9.7";
    private static Main main;
    private boolean running;
    private final ThreadPoolExecutor executor;
    private static Queue<Runnable> taskList = new LinkedList<>();
    private static Queue<UserInfo> userInfos = new LinkedList<>();
    private static int CORE_POOL_SIZE = 300;//同时执行线程数
    public Main(){
        running = true;
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                CORE_POOL_SIZE,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );
    }

    public static void main(String[] args) {
        File file = new File("config.properties");
        FileWriter writer;
        if (!file.exists()) {
            try {
                file.createNewFile();
                writer = new FileWriter(file, false);
                writer.append("#同时执行线程数\nthreads=300\n#数据库连接地址\nurl=localhost\n#数据库端口\nport=3306\n#库名\ndatabase=\n#数据库用户名\nuser=root\n#数据库密码\npassword=\n#打码平台秘钥\nsecretkey=\n#游戏版本号\nappVer=1.9.7\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CORE_POOL_SIZE = Integer.parseInt(props.getProperty("threads"));
        appVer = props.getProperty("appVer");
        main = new Main();
        //查询并缓存需要执行的订单
        main.loadOrders();
        while (!userInfos.isEmpty()){
            synchronized (userInfos) {
                main.executor.execute(new OrderExecute(userInfos.poll()));
            }
        }
        timer();
        timer2();
        while (main.isRunning()){

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //检查是否有新订单
            if (!userInfos.isEmpty()){
                synchronized (userInfos) {
                    main.executor.execute(new OrderExecute(userInfos.poll()));
                }
            }

            //检测任务列表并执行任务
            if (!taskList.isEmpty()){
                synchronized (taskList) {
                    taskList.poll().run();
                }
            }

        }
    }
    //自回体检查
    public static void timer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                loadOrdersRecover();
            }

        }, 1000 * 60 * 10,1000 * 60 * 10);
    }
    //新订单检查
    public static void timer2() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                loadOrdersRecover2();
            }

        }, 1000 * 60,1000 * 60);
    }

    public boolean isRunning(){
        return this.running;
    }

    public static void loadOrdersRecover(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        try {
            conn = getConnection();
            sql = "select * from `order` where `status`=4";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = sf.parse(rs.getString("timerecovery"));
                long ts = d.getTime();
                long time = System.currentTimeMillis() - ts;
                if(time > 1000*60*60*8){
                    sql = "update `order` set `status`=-1 where `order`=?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, rs.getString("order"));
                    ps.executeUpdate();
                    UserInfo userInfo = new UserInfo();
                    userInfo.setOrder(rs.getString("order"));
                    userInfo.setUsername(rs.getString("username"));
                    userInfo.setPwd(rs.getString("password"));
                    userInfo.setAccess_key(rs.getString("access_key"));
                    userInfo.setUid(rs.getString("uid"));
                    addToOrderQueue(userInfo);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
                rs.close();
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    public static void loadOrdersRecover2(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql;
        try {
            conn = getConnection();
            sql = "select * from `order` where `status`=0";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                sql = "update `order` set `status`=-1 where `order`=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, rs.getString("order"));
                ps.executeUpdate();
                UserInfo userInfo = new UserInfo();
                userInfo.setOrder(rs.getString("order"));
                userInfo.setUsername(rs.getString("username"));
                userInfo.setPwd(rs.getString("password"));
                userInfo.setAccess_key(rs.getString("access_key"));
                userInfo.setUid(rs.getString("uid"));
                addToOrderQueue(userInfo);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                conn.close();
                rs.close();
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }
    public void loadOrders(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "update `order` set `status`=-1 where `status`=0 or `status`=1";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            sql = "select * from `order` where `status`=-1";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                UserInfo userInfo = new UserInfo();
                userInfo.setOrder(rs.getString("order"));
                userInfo.setUsername(rs.getString("username"));
                userInfo.setPwd(rs.getString("password"));
                userInfo.setAccess_key(rs.getString("access_key"));
                userInfo.setUid(rs.getString("uid"));
                addToOrderQueue(userInfo);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                conn.close();
                rs.close();
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    public static void addToOrderQueue(UserInfo userInfo){
        synchronized (userInfos){
            userInfos.offer(userInfo);
        }
    }
}
