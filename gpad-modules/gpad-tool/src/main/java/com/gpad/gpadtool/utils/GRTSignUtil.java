package com.gpad.gpadtool.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Calendar;

/**
 * @BelongsProject: gpad-api
 * @BelongsPackage: com.gpad.gpadtool.utils
 * @Author: LF
 * @CreateTime: 2023-10-23
 * @Description: TODO
 * @Version: 1.0
 */
public class  GRTSignUtil {

//    @Value("${grt.param.app_key_grt}")
//    public static String APP_KEY_GRT;
//
//    @Value("${grt.param.secret_key_grt}")
//    public static String SECRET_KEY_GRT;
//
//    @Value("${grt.param.sign_timeout}")
//    public static int SIGN_TIMEOUT;

    // ak 客户端请求参数需要传
    public static String APP_KEY_GRT = "YONYOU_GRTXyt4onS9pNFfgOqAAjeYNL";
    // sk 客户端请求参数不需要传
    public static String SECRET_KEY_GRT = "2Ndealrough^0nature&%*954usickR(qn&3vprabbit5C^brought)OK!quiteCpain^5strongdFl0productD1osQdC";
    // 有效时长(单位：秒)
    public static int SIGN_TIMEOUT = 180;

    /**
     * 签名方法
     * @param appKey
     * @param timestamp
     * @param secretKey
     * @return
     */
    public static String sign(String appKey, Long timestamp, String secretKey) {
        return DigestUtils.md5Hex((appKey + "_" + timestamp + "_" + secretKey).toUpperCase()).toUpperCase();
    }

    public static boolean checkSign(String sign, String appKey, long timestamp) throws Exception {

        // 签名时间
        Calendar caleSign = Calendar.getInstance();
        try {
            caleSign.setTimeInMillis(timestamp);
			System.out.println("签名时间："+caleSign.getTimeInMillis());
        } catch (Exception e) {
            throw new Exception("签名时间格式错误：", e);
        }

        // 当前时间
        Calendar caleCurr = Calendar.getInstance();
//		System.out.println("当前时间："+caleCurr.getTimeInMillis());
        // 截至时间
        Calendar caleLast = Calendar.getInstance();
        caleLast.setTimeInMillis(timestamp);
        caleLast.add(Calendar.SECOND, SIGN_TIMEOUT);
		System.out.println("截至时间："+caleLast.getTimeInMillis());

        // 有效：截至时间 >= 当前时间 and 签名时间 <= 当前时间
        if(caleLast.getTimeInMillis() >= caleCurr.getTimeInMillis() && caleSign.getTimeInMillis() <= caleCurr.getTimeInMillis()) {

        }else {
            System.out.println("签名信息超时");
            return false;
        }

        // 验证签名信息是否有效
        String signString = GRTSignUtil.sign(appKey, timestamp, SECRET_KEY_GRT);
        if(!signString.equals(sign)) {
            System.out.println("签名信息错误");
            return false;
        }

        return true;
    }

    public static void main(String[] args) throws Exception {
            long timestamp = System.currentTimeMillis();
            System.out.println(timestamp);
            String sign = GRTSignUtil.sign(APP_KEY_GRT, timestamp, SECRET_KEY_GRT);
            System.out.println(sign);

            // appKey 提供给客户端
            String appKey = "YONYOU_GRTXyt4onS9pNFfgOqAAjeYNL";
            System.out.println(checkSign(sign, appKey, timestamp));
    }
}

