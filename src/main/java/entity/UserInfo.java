package entity;

import crypt.MD5;

import java.util.UUID;

public class UserInfo {
    private int is_new_user = 0;
    private String order;
    private String username;
    private String pwd;
    private String pwdRsa;
    private String access_key;
    private String uid;
    private String sid;
    private String udid;
    private String device_id = UUID.randomUUID().toString().toUpperCase();
    private String BUMA_OPEN_ID = "0";
    private String ViewerID = "0";
    private String RES_VER = "00000000";

    public int getIs_new_user() {
        return is_new_user;
    }

    public void setIs_new_user(int is_new_user) {
        this.is_new_user = is_new_user;
    }

    public String getRES_VER() {
        return RES_VER;
    }

    public void setRES_VER(String RES_VER) {
        this.RES_VER = RES_VER;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getPwdRsa() {
        return pwdRsa;
    }

    public void setPwdRsa(String pwdRsa) {
        this.pwdRsa = pwdRsa;
    }

    public String getAccess_key() {
        return access_key;
    }

    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getBUMA_OPEN_ID() {
        return BUMA_OPEN_ID;
    }

    public void setBUMA_OPEN_ID(String BUMA_OPEN_ID) {
        this.BUMA_OPEN_ID = BUMA_OPEN_ID;
    }

    public String getViewerID() {
        return ViewerID;
    }

    public void setViewerID(String viewerID) {
        ViewerID = viewerID;
    }
}
