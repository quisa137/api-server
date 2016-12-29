package com.jindata.apiserver.service.dao;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
 
import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
 
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
 
public class Crypto {
    private static String iv;
    private static Key keySpec;
    //아래의 값이 외부로 반출되면 보안상 큰 문제가 일어난다. 조심할 것
    private static final String key = "COM.JINDATA.#101_APISERVER";
    private static final String salt = KeyGenerators.string().generateKey();
    /*
    static {
        iv = key.substring(0, 16);
 
        byte[] keyBytes = new byte[16];
        byte[] b;
        try {
            b = key.getBytes("UTF-8");
            int len = b.length;
            if(len > keyBytes.length)
                len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len);
            keySpec = new SecretKeySpec(keyBytes, "AES");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }*/
 
    // 암호화
    public static String encrypt(String plaintext) {
        /*
        Cipher c;
        try {
            c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
            byte[] encrypted = c.doFinal(plaintext.getBytes("UTF-8"));
            String enStr = new String(Base64.encodeBase64(encrypted));
            return enStr;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException 
                | InvalidAlgorithmParameterException | IllegalBlockSizeException 
                | BadPaddingException | UnsupportedEncodingException e) {
            return null;
        }
        */
        /**
         * 아래의 구문을 실행 하려면 JCE가 필요함
         * 1. JDK8 에 가서 http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html 파일을 다운로드
         * 2. 압축을 풀고
         * 3. JDK_HOME/jre/lib/security 아래에 파일들을 카피해야 한다.
         * 별도 설정이 필요하나 보안성이 더 높다
         */
        TextEncryptor encryptor = Encryptors.text(key, salt);
        return encryptor.encrypt(plaintext);
    }
 
    //복호화
    public static String decrypt(String encryptedtext) {
        /*
        Cipher c;
        try {
            c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
            
            byte[] byteStr = Base64.decodeBase64(encryptedtext.getBytes());
     
            return new String(c.doFinal(byteStr),"UTF-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException 
                | InvalidAlgorithmParameterException | UnsupportedEncodingException 
                | IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
        */
        /**
         * 아래의 구문을 실행 하려면 JCE가 필요함
         * 1. JDK8 에 가서 http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html 파일을 다운로드
         * 2. 압축을 풀고
         * 3. JDK_HOME/jre/lib/security 아래에 관련 파일을 카피해야 한다.
         */
        TextEncryptor decryptor = Encryptors.text(key, salt);
        return decryptor.decrypt(encryptedtext);
    }
}