package crypt;

import org.apache.commons.codec.binary.Base64;

public class RSAc {

    public static String a(byte[] doFinal) {
        return  Base64.encodeBase64String(doFinal);
    }

    public static byte[] a(String arg3) {
        return Base64.decodeBase64(arg3);
    }
}
