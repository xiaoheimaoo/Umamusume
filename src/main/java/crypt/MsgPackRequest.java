package crypt;

import com.alibaba.fastjson.JSONObject;
import entity.UserInfo;
import java.util.Base64;
import java.util.UUID;
import static crypt.AES.COMMON_HEADER;
import static crypt.AES.COMMON_HEADER2;
import static crypt.HexString.byteArrayToHexString;
import static crypt.HexString.hexStringToByteArray;


public class MsgPackRequest {
    public static String encrypt(UserInfo userInfo, JSONObject json) {
        byte[] aes_key = hexStringToByteArray(MD5.encrypt(userInfo.getSid() + COMMON_HEADER2));
        byte[] aes_iv = hexStringToByteArray(MD5.encrypt(userInfo.getUdid() + COMMON_HEADER2));
        byte[] bytes = null;
        try {
            bytes = AES.encryptAES(aes_key,aes_iv,json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String random_data = MD5.encrypt(UUID.randomUUID().toString())+MD5.encrypt(UUID.randomUUID().toString());
        // 将十六进制字符串转换为字节数组
        byte[] bytes1 = hexStringToByteArray(userInfo.getSid() + userInfo.getUdid());
        byte[] bytes2 = hexStringToByteArray(COMMON_HEADER);
        byte[] bytes3 = hexStringToByteArray(random_data);

        // 对字节数组进行异或操作
        byte[] result = xorBytes(bytes1, bytes2, bytes3);

        // 将异或操作后的字节数组转换回十六进制字符串
        String hexResult = byteArrayToHexString(result);
        String header = hexResult + random_data;
        header = "40000000"+header;
        String request_data = header + byteArrayToHexString(bytes);
        return Base64.getEncoder().encodeToString(hexStringToByteArray(request_data));
    }
    public static JSONObject decrypt(String base) {
        String base64_data = byteArrayToHexString(Base64.getDecoder().decode(base));
        String cipher_data = base64_data.substring(136);
        String encrypt_key_info = base64_data.substring(8,72);
        String random_data = base64_data.substring(72,136);
        // 将十六进制字符串转换为字节数组
        byte[] bytes1 = hexStringToByteArray(encrypt_key_info);
        byte[] bytes2 = hexStringToByteArray(COMMON_HEADER);
        byte[] bytes3 = hexStringToByteArray(random_data);

        // 对字节数组进行异或操作
        String result = byteArrayToHexString(xorBytes(bytes1, bytes2, bytes3));
        byte[] aes_key = hexStringToByteArray(MD5.encrypt(result.substring(0,32) + COMMON_HEADER2));
        byte[] aes_iv = hexStringToByteArray(MD5.encrypt(result.substring(32) + COMMON_HEADER2));
        JSONObject js = null;
        try {
            js = AES.decryptAES(aes_key,aes_iv, hexStringToByteArray(cipher_data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return js;
    }
    private static byte[] xorBytes(byte[] bytes1, byte[] bytes2, byte[] bytes3) {
        byte[] result = new byte[Math.max(bytes1.length, Math.max(bytes2.length, bytes3.length))];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (bytes1[i] ^ bytes2[i] ^ bytes3[i]);
        }
        return result;
    }
}
