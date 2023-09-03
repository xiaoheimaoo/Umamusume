package crypt;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PwdRsa {
    public String a(String arg3, String arg4) {
        String res=null;
        try {
            PublicKey v0_1 = PwdRsa.b("RSA", arg4);
            Cipher v1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            v1.init(1, (Key)v0_1);
            res = RSAc.a(v1.doFinal(arg3.getBytes("UTF-8")));
        }
        catch(Exception e) {
            e.printStackTrace();
            res = null;
        }

        return res;
    }
    private static PublicKey b(String arg2, String arg3) {
        try {
            return KeyFactory.getInstance(arg2).generatePublic(new X509EncodedKeySpec(RSAc.a(arg3)));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String pwd(String arg1, String arg2) {
        String hash= arg1;
        String s1="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDjb4V7EidX/ym28t2ybo0U6t0n"+"\n"+
                "6p4ej8VjqKHg100va6jkNbNTrLQqMCQCAYtXMXXp2Fwkk6WR+12N9zknLjf+C9sx"+"\n"+
                "/+l48mjUU8RqahiFD1XT/u2e0m2EN029OhCgkHx3Fc/KlFSIbak93EH/XlYis0w+"+"\n"+
                "Xl69GV6klzgxW6d2xQIDAQAB"+"\n";
        PwdRsa rsa= new PwdRsa();
        String pwd= arg2;
        String lastpwd = rsa.a(hash+pwd, s1);
        return lastpwd;
    }

}

