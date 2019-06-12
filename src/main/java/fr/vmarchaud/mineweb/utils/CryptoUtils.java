package fr.vmarchaud.mineweb.utils;

import javax.crypto.*;
import javax.xml.bind.*;
import javax.crypto.spec.*;
import java.security.spec.*;
import java.security.*;

public class CryptoUtils
{
    public static String encryptAES(final String raw, final SecretKeySpec key, final String iv) throws Exception {
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, key, new IvParameterSpec(DatatypeConverter.parseBase64Binary(iv), 0, cipher.getBlockSize()));
        final byte[] ciphered = cipher.doFinal(raw.getBytes("UTF-8"));
        return DatatypeConverter.printBase64Binary(ciphered);
    }
    
    public static String decryptAES(final String raw, final SecretKeySpec key, final String iv) throws Exception {
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, key, new IvParameterSpec(DatatypeConverter.parseBase64Binary(iv), 0, cipher.getBlockSize()));
        return new String(cipher.doFinal(DatatypeConverter.parseBase64Binary(raw)), "UTF-8");
    }
    
    public static String encryptRSA(final String raw, final PublicKey key) throws Exception {
        final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(1, key);
        final byte[] ciphered = cipher.doFinal(raw.getBytes("UTF-8"));
        return DatatypeConverter.printBase64Binary(ciphered);
    }
    
    public static String decryptRSA(final String raw, final PublicKey key) throws Exception {
        final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(2, key);
        return new String(cipher.doFinal(DatatypeConverter.parseBase64Binary(raw)));
    }
}
