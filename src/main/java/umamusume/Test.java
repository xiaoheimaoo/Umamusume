package umamusume;

import entity.UserInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static utils.Hikari.getConnection;

public class Test {
    public void test(UserInfo userInfo) throws SQLException {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        Connection conn2 = getConnection();
        String sql2 = "update `order` set `message`='订单已完成',`status`=2,`complete`=? where `order`=? and `status`=1";
        PreparedStatement ps2 = conn2.prepareStatement(sql2);
        ps2.setString(1, dateStr);
        ps2.setString(2, userInfo.getOrder());
        ps2.executeUpdate();
        conn2.close();
        ps2.close();
    }
}
