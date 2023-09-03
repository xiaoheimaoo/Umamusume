package utils;

import api.BiliLogin;
import entity.UserInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static utils.Hikari.getConnection;

public class OrderExecute implements Runnable{

    private UserInfo userInfo;

    public OrderExecute(UserInfo userInfo){
        this.userInfo = userInfo;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(userInfo.getOrder());
        try {
            login();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-----------------");
            System.out.println(Thread.currentThread().getName());
            System.out.println("-----------------");
            Connection conn2 = getConnection();
            String sql2 = "update `order` set `message`='未知错误请重启订单！',`status`=3 where `order`=?";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, userInfo.getOrder());
                ps2.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }finally {
                try {
                    conn2.close();
                    ps2.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    public void login() {
        Connection conn2 = getConnection();
        String sql2 = "update `order` set `status`=1 where `order`=? and `status`!=1";
        PreparedStatement ps2 = null;
        try {
            ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, userInfo.getOrder());
            ps2.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                conn2.close();
                ps2.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        new BiliLogin().login(userInfo);
        OrderType.type(userInfo);
    }

}
