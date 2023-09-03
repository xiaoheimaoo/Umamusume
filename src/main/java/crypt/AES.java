package crypt;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;

public class AES {
    public static final String COMMON_HEADER = "2ea2480278210798975ae0a4be501c5c01b06ad54b5b84860e01c92d6ba15bd3";
    public static final String COMMON_HEADER2 = "d0cdfa7fd4ebfcf9bea71192b60b570bfc16f6c2";
    public static byte[] encryptAES(byte[] key,byte[] iv,JSONObject json) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(jsonMsgpack(json));
    }

    public static JSONObject decryptAES(byte[] key,byte[] iv,byte[] crypted) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return msgpackJson(cipher.doFinal(crypted));
    }
    public static byte[] jsonMsgpack(JSONObject json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
            byte[] bytes = objectMapper.writeValueAsBytes(json);
            return bytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static JSONObject msgpackJson(byte[] bytes) {
        MessageUnpacker msg = MessagePack.newDefaultUnpacker(bytes);
        try {
            JSONObject json = JSONObject.parseObject(msg.unpackValue().toString());
            return json;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
