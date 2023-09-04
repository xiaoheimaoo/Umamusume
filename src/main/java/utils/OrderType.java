package utils;

import entity.UserInfo;
import umamusume.Test;

import java.sql.SQLException;

public class OrderType {
    public static void type(UserInfo userInfo) {
        try {
            new Test().test(userInfo);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
