package com.gpad.gpadtool.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CryptoUtils {

    private static final String PUBLIC_KEY="publicKey";
    private static final String PRIVATE_KEY="privateKey";

////    public static void main(String[] args) {
////        try {
////            Map<String, String> stringStringMap = CryptoUtils.genKeyPair();
////            System.out.println(stringStringMap.toString());
////        } catch (NoSuchAlgorithmException e) {
////            e.printStackTrace();
////        }
////    }
//public static void main(String[] args) {
//    String str = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCA9qXKKnGqYu4Qmb5i0YN+8iWd927Yj0+451++PpoLiH0xiNPI3OOIRMvFTXXP+aouffEu/x/LFJ8xacOCFCRX9yRxde1SNlTQ5fwLPk/E3FA7DOiYIxa8luY4ky/DO5897nyw0GaxUMp1ITgslSDy1+ynP0H4ll5nN1wzxemGOwIDAQAB";
//    String pra = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAID2pcoqcapi7hCZvmLRg37yJZ33btiPT7jnX74+mguIfTGI08jc44hEy8VNdc/5qi598S7/H8sUnzFpw4IUJFf3JHF17VI2VNDl/As+T8TcUDsM6JgjFryW5jiTL8M7nz3ufLDQZrFQynUhOCyVIPLX7Kc/QfiWXmc3XDPF6YY7AgMBAAECgYB1sKFb/Z/9fosDW0sDo2/9Sb5KiqP9vctjUsNtOnQaUlTt/5BGnk6G7UGIrUf2pEFKbGTOHsVE72zYC00ASKFg0agy7tBlG9LQ97Y9PE8IEMpOLDVK/hTB6j74k8CZzBT4jYgI/Ko5/A1PihVYI4tr7L8WWheMybbNWiaZVN7JAQJBAOL7mvsvXv1XQS8Q5q/soanR6qvV36jTtW5yfBeH7FtfFveWggqGaF/WnOikYUyoGmTgksueZUh6TvONO2R3ot0CQQCRczGapGNZn9FqdR7caJj+XZUK5zOIO+fLt++hWh5SJdV0ovhgBiEOO7SCHDVUX5OWEJYxbBKLplvbNrYbEz/3AkEA1n9GWhDOB0KpirzCA4Xuz/hbd79I+NX4IkN2GRaNuVsG7ToB8chKYicBaKfOew5wwtumXV12ASPz8ByK/8o+0QJASlCuHpIxhOBocllHHFyBFy4fccOxi8D37Rmc4TcXWOelyYNfHkjjaM9Oqk/ZW7O9x6XP546NZzbnpfitUOYj/wJBALR9KJtKm5jxAdXaryV6vAMQUEwRzoM9sE3yecqqBaciQGXtkTX8EN8lHOuUO/IUxsIeQQiIgV7lB3vKEg2+aqI=";
//    try {
//        String s = CryptoUtils.publicKeyEncrypt("楚华", str);
//        System.out.println("加密后"+ s);
//        String s1 = CryptoUtils.privateKeyDecrypt(s, pra);
//        System.out.println("解密后"+ s1);
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//}
    /**
     * 	获取公私钥
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, String> genKeyPair() throws NoSuchAlgorithmException {
        System.out.println("开始生成公钥私钥对");
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();    // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();        // 得到公钥
        String publicKeyString = new String(Base64.getEncoder().encode(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.getEncoder().encode(privateKey.getEncoded()));
        // 将公钥和私钥保存到Map
        Map<String, String> map = new HashMap<>();
        map.put(PUBLIC_KEY, publicKeyString);
        map.put(PRIVATE_KEY, privateKeyString);
        return map;
    }
    /**
     * 	公钥加密
     * @param str
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String publicKeyEncrypt(String str, String publicKey) throws Exception {
        log.info("{}|RSA公钥加密前的数据|str:{}|publicKey:{}", str);
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").
                generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        //当长度过长的时候，需要分割后加密 117个字节
        byte[] resultBytes = getMaxResultEncrypt(str, cipher);

        String outStr = Base64.getEncoder().encodeToString(resultBytes);
        log.info("{}|公钥加密后的数据|outStr:{}", outStr);
        return outStr;
    }

    private static byte[] getMaxResultEncrypt(String str, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        byte[] inputArray = str.getBytes();
        int inputLength = inputArray.length;
        log.info("{}|加密字节数|inputLength:{}", inputLength);
        // 最大加密字节数，超出最大字节数需要分组加密
        int MAX_ENCRYPT_BLOCK = 117;
        // 标识
        int offSet = 0;
        byte[] resultBytes = {};
        byte[] cache = {};
        while (inputLength - offSet > 0) {
            if (inputLength - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(inputArray, offSet, MAX_ENCRYPT_BLOCK);
                offSet += MAX_ENCRYPT_BLOCK;
            } else {
                cache = cipher.doFinal(inputArray, offSet, inputLength - offSet);
                offSet = inputLength;
            }
            resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
            System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
        }
        return resultBytes;
    }

    /**
     * RSA私钥解密
     *
     * @author gggcgba 【wechat:13031016567】
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String privateKeyDecrypt(String str, String privateKey) throws Exception {
        log.info("{}|RSA私钥解密前的数据|str:{}|privateKey:{}", str);
        //64位解码加密后的字符串
//        byte[] inputByte = Base64.getDecoder().decode(str.getBytes("UTF-8"));
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
//        String outStr = new String(cipher.doFinal(inputByte));
        //当长度过长的时候，需要分割后解密 128个字节
        String outStr = new String(getMaxResultDecrypt(str, cipher));
        log.info("{}|RSA私钥解密后的数据|outStr:{}", outStr);
        return outStr;
    }

    private static byte[] getMaxResultDecrypt(String str, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        byte[] inputArray = Base64.getDecoder().decode(str.getBytes("UTF-8"));
        int inputLength = inputArray.length;
        log.info("{}|解密字节数|inputLength:{}", inputLength);
        // 最大解密字节数，超出最大字节数需要分组加密
        int MAX_ENCRYPT_BLOCK = 128;
        // 标识
        int offSet = 0;
        byte[] resultBytes = {};
        byte[] cache = {};
        while (inputLength - offSet > 0) {
            if (inputLength - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(inputArray, offSet, MAX_ENCRYPT_BLOCK);
                offSet += MAX_ENCRYPT_BLOCK;
            } else {
                cache = cipher.doFinal(inputArray, offSet, inputLength - offSet);
                offSet = inputLength;
            }
            resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
            System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
        }
        return resultBytes;
    }
}

