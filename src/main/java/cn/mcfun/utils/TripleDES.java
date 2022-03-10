package cn.mcfun.utils;

import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class TripleDES {
    final static String key = "b5nHjsMrqaeNliSs3jyOzgpD";
    final static String keyiv = "wuD6keVr";
    public String decryptMode(String data) {
        byte[] keyB = key.getBytes();
        byte[] keyivB = keyiv.getBytes();
        byte[] dataByte = Base64.getDecoder().decode(data);
        String result = null;
        try {
            DESedeKeySpec spec = new DESedeKeySpec(keyB);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            Key deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(keyivB);
            cipher.init(2, deskey, ips);
            byte[] bOut = cipher.doFinal(dataByte);
            result = new String(bOut, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public String encryptMode(String data) {
        byte[] keyB = key.getBytes();
        byte[] keyivB = keyiv.getBytes();
        byte[] dataByte = data.getBytes();
        String result = null;
        try {
            DESedeKeySpec spec = new DESedeKeySpec(keyB);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            Key deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(keyivB);
            cipher.init(1, deskey, ips);
            byte[] bOut = cipher.doFinal(dataByte);
            result = Base64.getEncoder().encodeToString(bOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

}
