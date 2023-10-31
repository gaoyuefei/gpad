package com.gpad.gpadtool.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RsaUtils {
    private static Logger logger = LoggerFactory.getLogger(RsaUtils.class);
    private static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjGTyMPIG3P+ebXJeZi1fLkqW67JH93gGKZsO5lvv6BVkr/MReBtCDy3VnovY4JUxJJbEj6sfcMoHK0yC3pfDGhuEP3blOQWkwg4IcDSHUhAiZqtQQ34UkZYCTxP1g2fLhR7W/JUf6eA5rNM9l1MJuErS0ijEBtB9IiK6QUlw4lRt+9qe1SuK7oGoai6eyfW7yvPvS9EPIoFxZFzl7GsUFNcPjctMs7Gw4AveS+uM42P2yMGTsSrh9oEUmin+8lOYgxfJYLtHND6Nba/Hzfjxx/yVqcfiCVs9QIoqJXfisy2XDnbiC9vFlM1B45EyhbL8b4DDdnc20VZKnQJD/wM0vwIDAQAB";

    public static KeyPair getKeyPair() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes("utf-8"));
        keyPairGenerator.initialize(2048, secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    public static String getPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static PublicKey string2PublicKey(String pubStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Base64.getDecoder().decode(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static byte[] publicEncrytype(byte[] content, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(1, publicKey);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }

    public static String getPublicKey() {
        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjGTyMPIG3P+ebXJeZi1fLkqW67JH93gGKZsO5lvv6BVkr/MReBtCDy3VnovY4JUxJJbEj6sfcMoHK0yC3pfDGhuEP3blOQWkwg4IcDSHUhAiZqtQQ34UkZYCTxP1g2fLhR7W/JUf6eA5rNM9l1MJuErS0ijEBtB9IiK6QUlw4lRt+9qe1SuK7oGoai6eyfW7yvPvS9EPIoFxZFzl7GsUFNcPjctMs7Gw4AveS+uM42P2yMGTsSrh9oEUmin+8lOYgxfJYLtHND6Nba/Hzfjxx/yVqcfiCVs9QIoqJXfisy2XDnbiC9vFlM1B45EyhbL8b4DDdnc20VZKnQJD/wM0vwIDAQAB";
    }

}