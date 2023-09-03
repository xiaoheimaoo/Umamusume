package api;

import com.alibaba.fastjson.JSONObject;
import crypt.MD5;
import crypt.MsgPackRequest;
import crypt.MsgPackResponse;
import entity.UserInfo;
import request.PostRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import static utils.Hikari.getConnection;

public class Tutorial {
    public void skip(UserInfo userInfo) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'+08:00'");
        String buma_client_time = sf.format(System.currentTimeMillis());
        String params = MsgPackRequest.encrypt(userInfo, JSONObject.parseObject("{\"device_id\":\""+userInfo.getDevice_id()+"\",\"b_zone\":\"CN\",\"channel\":\"1000\",\"graphics_device_name\":\"Apple A12 GPU\",\"ip_address\":\"192.168.2.101\",\"locale\":\"JPN\",\"buma_client_time\":\""+buma_client_time+"\",\"device_name\":\"iPad11,1\",\"carrier\":\"\",\"keychain\":0,\"b_device_type\":1,\"viewer_id\":"+userInfo.getViewerID()+",\"device\":1,\"platform_os_version\":\"iPadOS 16.6\"}"));
        String result = PostRequest.sendPost(userInfo, "https://le1-prod-bili-gs-uma.bilibiligame.net/tutorial/skip", params);
        JSONObject jsonObject = MsgPackResponse.decrypt(userInfo,result);
        if (jsonObject.getIntValue("response_code") == 1) {
            userInfo.setSid(MD5.getSID(jsonObject.getJSONObject("data_headers").getString("sid")));
            Connection conn2 = getConnection();
            String sql2 = "update `order` set `message`='跳过新手教程完成' where `order`=? and `status`=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, userInfo.getOrder());
                ps2.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                try {
                    conn2.close();
                    ps2.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        } else {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message=? where `order`=? and status=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, jsonObject.toString());
                ps2.setString(2, userInfo.getOrder());
                ps2.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                try {
                    conn2.close();
                    ps2.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            Thread.currentThread().stop();
        }
    }

}
