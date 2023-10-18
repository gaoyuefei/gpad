package com.gpad.gpadtool.utils;

import org.apache.logging.log4j.util.Base64Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class UuidUtil {

    /**
     * 生成 32 位的随机码
     */
    public static String generateUuid(){
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-","");
        return uuid;
    }

    /**
     * 生成 32 位的带日期随机码
     */
    public static String generateUuidWithDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-","");
        uuid = simpleDateFormat.format(new Date()).concat(uuid).substring(0,32);
        return uuid;
    }

    /**
     * 生成token
     */
    public static String generateToken(String key){
        return Base64Util.encode(generateUuidWithDate().concat("_").concat(key));
    }

    public static void main(String[] args) {
        for (int i = 0;i < 100; i++) {
            System.out.println(UuidUtil.generateUuidWithDate());
        }
    }

}
