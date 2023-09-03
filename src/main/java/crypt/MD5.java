package crypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static crypt.HexString.hexStringToByteArray;

public class MD5 {
    public static String encrypt(String key){
        char hexDigests[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        try {
            byte[] in = hexStringToByteArray(key);
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            messageDigest.update(in);
            byte[] md = messageDigest.digest();
            int j = md.length;
            char[] str = new char[j*2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte b = md[i];
                str[k++] = hexDigests[b >>> 4 & 0xf];
                str[k++] = hexDigests[b & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("md5加密失败",e);
        }
    }
    public static String md5Utils(String str){
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            m.update(str.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte b[] = m.digest();
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toHexString((0x000000FF & b[i]) | 0xFFFFFF00).substring(6);
        }
        return result;
    }
    public static String getSID(String str){
        str = str + "sK5R8VeFU4";
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            m.update(str.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte b[] = m.digest();
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toHexString((0x000000FF & b[i]) | 0xFFFFFF00).substring(6);
        }
        return result;
    }
}
