package crypt;

import com.alibaba.fastjson.JSONObject;
import entity.UserInfo;
import java.util.Base64;

import static crypt.AES.COMMON_HEADER2;
import static crypt.HexString.byteArrayToHexString;
import static crypt.HexString.hexStringToByteArray;

public class MsgPackResponse {
    public static JSONObject decrypt(UserInfo userInfo, String base) {
        String base64_data = byteArrayToHexString(Base64.getDecoder().decode(base));
/*        String header_len = base64_data.substring(0,8);
        String header = base64_data.substring(8,72);*/
        String cipher_data = base64_data.substring(72);
        byte[] aes_key = hexStringToByteArray(MD5.encrypt(userInfo.getSid() + COMMON_HEADER2));
        byte[] aes_iv = hexStringToByteArray(MD5.encrypt(userInfo.getUdid() + COMMON_HEADER2));
        JSONObject js = null;
        try {
            js = AES.decryptAES(aes_key,aes_iv, hexStringToByteArray(cipher_data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return js;
    }
}
