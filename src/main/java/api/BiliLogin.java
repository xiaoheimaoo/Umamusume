package api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import crypt.*;
import entity.UserInfo;
import org.apache.http.message.BasicNameValuePair;
import request.GetRequest;
import request.PostRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import static crypt.LoginSign.getLoginSign;
import static utils.Hikari.getConnection;

public class BiliLogin {
    public void login(UserInfo userInfo){
        userInfo.setSid(MD5.getSID(userInfo.getViewerID()+userInfo.getDevice_id().toLowerCase()));
        userInfo.setUdid(userInfo.getDevice_id().replace("-","").toLowerCase());
        if(userInfo.getAccess_key() == null){
            String hash = getCipher(userInfo);
            String pwd = PwdRsa.pwd(hash, userInfo.getPwd());
            userInfo.setPwdRsa(pwd);
            loginPwd(userInfo);
        }else{
            loginToken(userInfo);
        }
        signup(userInfo);
        start_session(userInfo);
        loadIndex(userInfo);
        if(userInfo.getIs_new_user() == 1){
            User user = new User();
            user.change_name(userInfo);
            user.change_sex(userInfo);
            new Tutorial().skip(userInfo);
            loadIndex(userInfo);
            Present present = new Present();
            present.index(userInfo);
            present.receive_all(userInfo);
        }
    }
    public String getCipher(UserInfo userInfo) {
        String result;
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("bd_id", "E1E49DA4-10E5-4C05-9D5B-41906D6E9F90-47D49F88-4869-43CA-9320-B71"));
        params.add(new BasicNameValuePair("c", "1"));
        params.add(new BasicNameValuePair("channel_id", "1000"));
        params.add(new BasicNameValuePair("cipher_type", "bili_login_rsa"));
        params.add(new BasicNameValuePair("domain", "line1-sdk-center-login-sh.biligame.net"));
        params.add(new BasicNameValuePair("domain_switch_count", "0"));
        params.add(new BasicNameValuePair("game_id", "125"));
        params.add(new BasicNameValuePair("merchant_id", "1"));
        params.add(new BasicNameValuePair("req_method", "/api/external/issue/cipher/v3"));
        params.add(new BasicNameValuePair("request_id", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("sdk_log_type", "3"));
        params.add(new BasicNameValuePair("sdk_type", "2"));
        params.add(new BasicNameValuePair("sdk_ver", "5.9.5"));
        params.add(new BasicNameValuePair("server_id", "5478"));
        params.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
        params.add(new BasicNameValuePair("udid", userInfo.getDevice_id()));
        params.add(new BasicNameValuePair("version", "3"));
        String sign = getLoginSign(params);
        params.add(new BasicNameValuePair("sign", sign));
        result = PostRequest.sendPost(userInfo, "https://line1-sdk-center-login-sh.biligame.net/api/external/issue/cipher/v3", params);
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message='触发安全风控策略，请尝试重启' where `order`=?";
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
            Thread.currentThread().stop();
        }
        if (result.contains("\"code\":0")) {
            String hash = jsonObject.getString("hash");
            Connection conn2 = getConnection();
            String sql2 = "update `order` set message='获取服务器公钥完成' where `order`=? and status=1";
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
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return hash;
        } else {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message=? where `order`=? and status=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, jsonObject.getString("message"));
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
            return null;
        }
    }
    public void loginPwd(UserInfo userInfo) {
        String result;
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("bd_id", "E1E49DA4-10E5-4C05-9D5B-41906D6E9F90-47D49F88-4869-43CA-9320-B71"));
        params.add(new BasicNameValuePair("c", "1"));
        params.add(new BasicNameValuePair("channel_id", "1000"));
        params.add(new BasicNameValuePair("domain", "line1-sdk-center-login-sh.biligame.net"));
        params.add(new BasicNameValuePair("domain_switch_count", "0"));
        params.add(new BasicNameValuePair("game_id", "125"));
        params.add(new BasicNameValuePair("merchant_id", "1"));
        params.add(new BasicNameValuePair("pwd", userInfo.getPwdRsa()));
        params.add(new BasicNameValuePair("req_method", "/api/external/login/v3"));
        params.add(new BasicNameValuePair("request_id", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("sdk_log_type", "3"));
        params.add(new BasicNameValuePair("sdk_type", "2"));
        params.add(new BasicNameValuePair("sdk_ver", "5.9.5"));
        params.add(new BasicNameValuePair("server_id", "5478"));
        params.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
        params.add(new BasicNameValuePair("udid", userInfo.getDevice_id()));
        params.add(new BasicNameValuePair("user_id", userInfo.getUsername()));
        params.add(new BasicNameValuePair("version", "3"));
        String sign = getLoginSign(params);
        params.add(new BasicNameValuePair("sign", sign));
        result = PostRequest.sendPost(userInfo, "https://line1-sdk-center-login-sh.biligame.net/api/external/login/v3", params);
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message='触发安全风控策略，请尝试重启' where `order`=?";
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
            Thread.currentThread().stop();
        }
        if (result.contains("\"code\":0")) {
            String access_key = jsonObject.getString("access_key");
            String uid = jsonObject.getString("uid");
            userInfo.setAccess_key(access_key);
            userInfo.setUid(uid);
            userInfo.setBUMA_OPEN_ID(uid);
            Connection conn2 = getConnection();
            String sql2 = "update `order` set `access_key`=?,`uid`=?,`message`='验证账号密码完成' where `order`=? and `status`=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, access_key);
                ps2.setString(2, uid);
                ps2.setString(3, userInfo.getOrder());
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
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (result.contains("\"code\":500002")) {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message='用户名或密码错误' where `order`=? and status=1";
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
            Thread.currentThread().stop();
        } else if (result.contains("200007") || result.contains("\"code\":200000") || result.contains("\"code\":-500")) {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set message='尝试获取验证码' where `order`=? and status=1";
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
            String captcha[] = captcha(userInfo);
            String geetest[] = geetest(captcha[0], captcha[1], userInfo);
            String gt_user_id = captcha[2];
            String captcha_type = captcha[3];
            String challenge = geetest[0];
            String validate = geetest[1];
            logincaptcha(gt_user_id, challenge, validate, captcha_type, userInfo);
        } else if (result.contains("\"code\":-662")) {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set message='重新获取服务器公钥' where `order`=? and status=1";
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
            String hash = getCipher(userInfo);
            userInfo.setPwdRsa(PwdRsa.pwd(hash, userInfo.getPwd()));
            loginPwd(userInfo);
        } else {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message=? where `order`=? and status=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, jsonObject.getString("message"));
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
    public void loginToken(UserInfo userInfo) {
        String result;
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("bd_id", "E1E49DA4-10E5-4C05-9D5B-41906D6E9F90-47D49F88-4869-43CA-9320-B71"));
        params.add(new BasicNameValuePair("c", "1"));
        params.add(new BasicNameValuePair("channel_id", "1000"));
        params.add(new BasicNameValuePair("domain", "line1-sdk-center-login-sh.biligame.net"));
        params.add(new BasicNameValuePair("domain_switch_count", "0"));
        params.add(new BasicNameValuePair("game_id", "125"));
        params.add(new BasicNameValuePair("merchant_id", "1"));
        params.add(new BasicNameValuePair("access_key", userInfo.getAccess_key()));
        params.add(new BasicNameValuePair("req_method", "/api/external/user.token.oauth.login/v3"));
        params.add(new BasicNameValuePair("request_id", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("sdk_log_type", "3"));
        params.add(new BasicNameValuePair("sdk_type", "2"));
        params.add(new BasicNameValuePair("sdk_ver", "5.9.5"));
        params.add(new BasicNameValuePair("server_id", "5478"));
        params.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
        params.add(new BasicNameValuePair("udid", userInfo.getDevice_id()));
        params.add(new BasicNameValuePair("uid", userInfo.getUid()));
        params.add(new BasicNameValuePair("version", "3"));
        String sign = getLoginSign(params);
        params.add(new BasicNameValuePair("sign", sign));
        result = PostRequest.sendPost(userInfo, "https://line1-sdk-center-login-sh.biligame.net/api/external/user.token.oauth.login/v3", params);
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message='触发安全风控策略，请尝试重启' where `order`=?";
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
            Thread.currentThread().stop();
        }
        if (result.contains("\"code\":0")) {
            userInfo.setAccess_key(jsonObject.getString("access_key"));
            userInfo.setUid(jsonObject.getString("uid"));
            userInfo.setBUMA_OPEN_ID(jsonObject.getString("uid"));
            Connection conn2 = getConnection();
            String sql2 = "update `order` set `access_key`=?,`uid`=?,`message`='使用access_key登录' where `order`=? and `status`=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, userInfo.getAccess_key());
                ps2.setString(2, userInfo.getUid());
                ps2.setString(3, userInfo.getOrder());
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
        } else if (result.contains("\"code\":500002")) {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message='用户名或密码错误' where `order`=? and status=1";
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
            Thread.currentThread().stop();
        } else if (result.contains("200007") || result.contains("\"code\":200000") || result.contains("\"code\":-500")) {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set message='尝试获取验证码' where `order`=? and status=1";
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
            String captcha[] = captcha(userInfo);
            String geetest[] = geetest(captcha[0], captcha[1], userInfo);
            String gt_user_id = captcha[2];
            String captcha_type = captcha[3];
            String challenge = geetest[0];
            String validate = geetest[1];
            logincaptcha(gt_user_id, challenge, validate, captcha_type, userInfo);
        } else {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set `access_key`=NULL,`uid`=NULL,`status`=0,`message`=? where `order`=? and `status`=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, jsonObject.getString("message"));
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
    public String[] captcha(UserInfo userInfo) {
        String result;
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("c", "1"));
        params.add(new BasicNameValuePair("channel_id", "1000"));
        params.add(new BasicNameValuePair("domain", "line1-sdk-center-login-sh.biligame.net"));
        params.add(new BasicNameValuePair("domain_switch_count", "0"));
        params.add(new BasicNameValuePair("game_id", "125"));
        params.add(new BasicNameValuePair("merchant_id", "1"));
        params.add(new BasicNameValuePair("req_method", "/api/client/start_captcha"));
        params.add(new BasicNameValuePair("sdk_log_type", "3"));
        params.add(new BasicNameValuePair("sdk_type", "2"));
        params.add(new BasicNameValuePair("request_id", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("sdk_ver", "5.9.5"));
        params.add(new BasicNameValuePair("server_id", "5478"));
        params.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
        params.add(new BasicNameValuePair("udid", userInfo.getDevice_id()));
        params.add(new BasicNameValuePair("version", "1"));
        String sign = getLoginSign(params);
        params.add(new BasicNameValuePair("sign", sign));
        result = PostRequest.sendPost(userInfo, "https://line1-sdk-center-login-sh.biligame.net/api/client/start_captcha", params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (result.contains("\"code\":0")) {
            String challenge = jsonObject.getString("challenge");
            String gt = jsonObject.getString("gt");
            String gt_user_id = jsonObject.getString("gt_user_id");
            String captcha_type = jsonObject.getString("captcha_type");
            String[] arr = {challenge, gt, gt_user_id, captcha_type};
            Connection conn2 = getConnection();
            String sql2 = "update `order` set message='获取验证码中' where `order`=? and status=1";
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
            return arr;
        } else {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message=? where `order`=? and status=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, jsonObject.getString("message"));
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
            return null;
        }
    }
    public String[] geetest(String challenge0, String gt, UserInfo userInfo) {
        String result;
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appkey", props.getProperty("secretkey")));
        params.add(new BasicNameValuePair("gt", gt));
        params.add(new BasicNameValuePair("challenge", challenge0));
        params.add(new BasicNameValuePair("referer", "https://game.bilibili.com/sdk/geetest/"));
        params.add(new BasicNameValuePair("sharecode", "aab5e7af8b2e4cc9a6e514998227e79c"));
        result = GetRequest.sendGet(props.getProperty("secretkey") + "?gt=" + gt + "&challenge=" + challenge0);
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=0,message=? where `order`=? and status=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, "验证码识别失败，正在重新登录！");
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
        if (jsonObject.getString("result").equals("success")) {
            String challenge = challenge0;
            String validate = jsonObject.getString("validate");
            String[] arr = {challenge, validate};
            Connection conn2 = getConnection();
            String sql2 = "update `order` set message='获取验证码成功' where `order`=? and status=1";
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
            return arr;
        } else {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=0,message=? where `order`=? and status=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, "验证码识别失败，正在重新登录！");
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
            return null;
        }
    }
    public void logincaptcha(String gt_user_id, String challenge, String validate, String captcha_type, UserInfo userInfo) {
        String result;
        String hash = getCipher(userInfo);
        String pwd = PwdRsa.pwd(hash, userInfo.getPwd());
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("bd_id", "E1E49DA4-10E5-4C05-9D5B-41906D6E9F90-47D49F88-4869-43CA-9320-B71"));
        params.add(new BasicNameValuePair("c", "1"));
        params.add(new BasicNameValuePair("ctoken", "6a9c0caba31041db87c69451c87801bd"));
        params.add(new BasicNameValuePair("channel_id", "1000"));
        params.add(new BasicNameValuePair("domain", "line1-sdk-center-login-sh.biligame.net"));
        params.add(new BasicNameValuePair("domain_switch_count", "0"));
        params.add(new BasicNameValuePair("game_id", "125"));
        params.add(new BasicNameValuePair("merchant_id", "1"));
        params.add(new BasicNameValuePair("pwd", pwd));
        params.add(new BasicNameValuePair("req_method", "/api/external/login/v3"));
        params.add(new BasicNameValuePair("request_id", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("sdk_log_type", "3"));
        params.add(new BasicNameValuePair("sdk_type", "2"));
        params.add(new BasicNameValuePair("sdk_ver", "5.9.5"));
        params.add(new BasicNameValuePair("server_id", "5478"));
        params.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
        params.add(new BasicNameValuePair("udid", userInfo.getDevice_id()));
        params.add(new BasicNameValuePair("user_id", userInfo.getUsername()));
        params.add(new BasicNameValuePair("version", "3"));
        params.add(new BasicNameValuePair("captcha_type", captcha_type));
        params.add(new BasicNameValuePair("challenge", challenge));
        params.add(new BasicNameValuePair("seccode", validate + "|jordan"));
        params.add(new BasicNameValuePair("validate", validate));
        params.add(new BasicNameValuePair("gt_user_id", gt_user_id));
        String sign = getLoginSign(params);
        params.add(new BasicNameValuePair("sign", sign));
        result = PostRequest.sendPost(userInfo, "https://line1-sdk-center-login-sh.biligame.net/api/external/login/v3", params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (result.contains("\"code\":0")) {
            String access_key = jsonObject.getString("access_key");
            String uid = jsonObject.getString("uid");
            userInfo.setAccess_key(access_key);
            userInfo.setUid(uid);
            userInfo.setBUMA_OPEN_ID(uid);
            Connection conn2 = getConnection();
            String sql2 = "update `order` set `access_key`=?,`uid`=?,`message`='验证账号密码完成' where `order`=? and `status`=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, access_key);
                ps2.setString(2, uid);
                ps2.setString(3, userInfo.getOrder());
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
        } else if (result.contains("\"code\":500002")) {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message='用户名或密码错误' where `order`=? and status=1";
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
            Thread.currentThread().stop();
        } else {
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message=? where `order`=? and status=1";
            PreparedStatement ps2 = null;
            String message = jsonObject.getString("message");
            if (message == null) {
                message = jsonObject.toString();
            }
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, message);
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
    public void signup(UserInfo userInfo) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'+08:00'");
        String buma_client_time = sf.format(System.currentTimeMillis());
        String params = MsgPackRequest.encrypt(userInfo,JSONObject.parseObject("{\"error_message\":\"\",\"buma_access_token\":\""+userInfo.getAccess_key()+"\",\"device_id\":\""+userInfo.getDevice_id()+"\",\"b_zone\":\"CN\",\"buma_uid\":\""+userInfo.getBUMA_OPEN_ID()+"\",\"channel\":\"1000\",\"graphics_device_name\":\"Apple A12 GPU\",\"ip_address\":\"192.168.2.101\",\"locale\":\"JPN\",\"buma_client_time\":\""+buma_client_time+"\",\"device_name\":\"iPad11,1\",\"carrier\":\"\",\"keychain\":0,\"credential\":\"\",\"b_device_type\":1,\"viewer_id\":0,\"error_code\":0,\"device\":1,\"platform_os_version\":\"iPadOS 16.6\"}"));
        String result = PostRequest.sendPost(userInfo, "https://le1-prod-bili-gs-uma.bilibiligame.net/tool/signup", params);
        JSONObject jsonObject = MsgPackResponse.decrypt(userInfo,result);
        if (jsonObject.getIntValue("response_code") == 1 && jsonObject.getJSONObject("data").getIntValue("is_new_user") == 0) {
            userInfo.setViewerID(jsonObject.getJSONObject("data").getString("viewer_id"));
            userInfo.setSid(MD5.getSID(userInfo.getViewerID()+userInfo.getDevice_id().toLowerCase()));
            Connection conn2 = getConnection();
            String sql2 = "update `order` set `message`='获取viewer_id完成' where `order`=? and `status`=1";
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
        }else if (jsonObject.getIntValue("response_code") == 1 && jsonObject.getJSONObject("data").getIntValue("is_new_user") == 1) {
            userInfo.setViewerID(jsonObject.getJSONObject("data").getString("viewer_id"));
            userInfo.setSid(MD5.getSID(userInfo.getViewerID()+userInfo.getDevice_id().toLowerCase()));
            userInfo.setIs_new_user(1);
            Connection conn2 = getConnection();
            String sql2 = "update `order` set `message`='为新玩家跳过新手教程' where `order`=? and `status`=1";
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
    public void start_session(UserInfo userInfo) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'+08:00'");
        String buma_client_time = sf.format(System.currentTimeMillis());
        String params = MsgPackRequest.encrypt(userInfo,JSONObject.parseObject("{\"device_id\":\""+userInfo.getDevice_id()+"\",\"b_zone\":\"CN\",\"channel\":\"1000\",\"graphics_device_name\":\"Apple A12 GPU\",\"ip_address\":\"192.168.2.101\",\"locale\":\"JPN\",\"buma_client_time\":\""+buma_client_time+"\",\"device_name\":\"iPad11,1\",\"carrier\":\"\",\"keychain\":0,\"b_device_type\":1,\"viewer_id\":"+userInfo.getViewerID()+",\"device\":1,\"platform_os_version\":\"iPadOS 16.6\"}"));
        String result = PostRequest.sendPost(userInfo, "https://le1-prod-bili-gs-uma.bilibiligame.net/tool/start_session", params);
        JSONObject jsonObject = MsgPackResponse.decrypt(userInfo,result);
        if (jsonObject.getIntValue("response_code") == 1) {
            userInfo.setSid(MD5.getSID(jsonObject.getJSONObject("data_headers").getString("sid")));
            userInfo.setRES_VER(jsonObject.getJSONObject("data").getString("resource_version"));
            Connection conn2 = getConnection();
            String sql2 = "update `order` set `message`='登录游戏完成' where `order`=? and `status`=1";
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
    public void loadIndex(UserInfo userInfo) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'+08:00'");
        String buma_client_time = sf.format(System.currentTimeMillis());
        String params = MsgPackRequest.encrypt(userInfo,JSONObject.parseObject("{\"device_id\":\""+userInfo.getDevice_id()+"\",\"b_zone\":\"CN\",\"channel\":\"1000\",\"graphics_device_name\":\"Apple A12 GPU\",\"ip_address\":\"192.168.2.101\",\"locale\":\"JPN\",\"buma_client_time\":\""+buma_client_time+"\",\"device_name\":\"iPad11,1\",\"carrier\":\"\",\"buma_boot_from_share\":false,\"keychain\":0,\"b_device_type\":1,\"viewer_id\":"+userInfo.getViewerID()+",\"device\":1,\"platform_os_version\":\"iPadOS 16.6\"}"));
        String result = PostRequest.sendPost(userInfo, "https://le1-prod-bili-gs-uma.bilibiligame.net/load/index", params);
        JSONObject jsonObject = MsgPackResponse.decrypt(userInfo,result);
        if (jsonObject.getIntValue("response_code") == 1) {
            userInfo.setSid(MD5.getSID(jsonObject.getJSONObject("data_headers").getString("sid")));
            String fcoin = jsonObject.getJSONObject("data").getJSONObject("coin_info").getString("fcoin");
            String coin = jsonObject.getJSONObject("data").getJSONObject("coin_info").getString("coin");
            String gacha = null;
            String exchange = null;
            JSONArray item_list = jsonObject.getJSONObject("data").getJSONArray("item_list");
            for(int i=0; i< item_list.size(); i++){
                if(item_list.getJSONObject(i).getString("item_id").equals("114")){
                    gacha = item_list.getJSONObject(i).getString("number");
                }else if(item_list.getJSONObject(i).getString("item_id").equals("130")){
                    exchange = item_list.getJSONObject(i).getString("number");
                }
            }
            Connection conn2 = getConnection();
            String sql2 = "update `order` set `fcoin`=?,`coin`=?,`gacha`=?,`exchange`=?,`message`='初始化游戏数据' where `order`=? and `status`=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, fcoin);
                ps2.setString(2, coin);
                ps2.setString(3, gacha);
                ps2.setString(4, exchange);
                ps2.setString(5, userInfo.getOrder());
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
        } else if(jsonObject.getIntValue("response_code") == 1 && jsonObject.getJSONObject("data").getJSONObject("user_info").getInteger("tutorial_step") < 1000){
            Connection conn2 = getConnection();
            String sql2 = "update `order` set status=3,message=? where `order`=? and status=1";
            PreparedStatement ps2 = null;
            try {
                ps2 = conn2.prepareStatement(sql2);
                ps2.setString(1, "新手教程未完成");
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
