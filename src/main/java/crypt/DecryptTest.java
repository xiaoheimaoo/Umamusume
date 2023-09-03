package crypt;

import com.alibaba.fastjson.JSONObject;

import java.util.Base64;
import static crypt.AES.COMMON_HEADER;
import static crypt.AES.COMMON_HEADER2;
import static crypt.HexString.byteArrayToHexString;
import static crypt.HexString.hexStringToByteArray;

public class DecryptTest {
    public static void decrypt(String base) {
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
        System.out.println(js);
    }
    public static void decrypt(String base,String base2) {
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
        System.out.println(js);
        decrypt2(aes_key,aes_iv, base2);
    }
    public static void decrypt2(byte[] aes_key,byte[] aes_iv,String base) {
        String base64_data = byteArrayToHexString(Base64.getDecoder().decode(base));
        String cipher_data = base64_data.substring(72);
        JSONObject js = null;
        try {
            js = AES.decryptAES(aes_key,aes_iv, hexStringToByteArray(cipher_data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(js);
    }
    private static byte[] xorBytes(byte[] bytes1, byte[] bytes2, byte[] bytes3) {
        byte[] result = new byte[Math.max(bytes1.length, Math.max(bytes2.length, bytes3.length))];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (bytes1[i] ^ bytes2[i] ^ bytes3[i]);
        }
        return result;
    }

}
