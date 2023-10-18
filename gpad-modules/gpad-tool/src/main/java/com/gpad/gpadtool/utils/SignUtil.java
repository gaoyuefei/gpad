package com.gpad.gpadtool.utils;


import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.constant.StatusCode;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.common.core.utils.sign.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 签名
 */
public class SignUtil {

    private static final Logger log = LoggerFactory.getLogger(SignUtil.class);


    public static String checkSign(HttpServletRequest request,String secretKey,Object body){
        String url = request.getRequestURI();

        String sign = request.getHeader("sign");
        String timestamp = request.getHeader("timestamp");
        String appId = request.getHeader("appId");

        String result = checkSignIsValid(sign, timestamp, appId);
        if (result != null) {
            log.info("签名认证失败：" + JSONObject.toJSONString(result));
            return "签名认证失败：" + JSONObject.toJSONString(result);
        }


        // body参数为空，获取Parameter的数据
        if (body == null) {
            body = getUrlPram(request);
        }

        try {
            boolean validPostRequest = SignUtil.isValidRequest(sign, body, appId, secretKey, Long.valueOf(timestamp), request.getMethod());
            if (!validPostRequest) {

                log.info("{}  认证失败 参数:sign = {} , url = {} ,请求参数 = {} ,传来的appId = {}, 配置的secretKey = {} , timestamp ={}",
                        request.getMethod(),
                        sign,
                        url,
                        body,
                        appId,
                        secretKey,
                        timestamp);

                return "签名认证失败 ";
            }
        } catch (Exception e) {
            log.error("验签出错! {}",e.getMessage());
            return "验签出错! {}" + e.getMessage();
        }

        return null;
    }


    private static String getUrlPram(HttpServletRequest request) {
        Enumeration<?> pNames = request.getParameterNames();

        StringBuilder sb = new StringBuilder();
        while (pNames.hasMoreElements()) {
            String pName = (String) pNames.nextElement();

            String pValue = request.getParameter(pName);

            if (sb.length() == 0) {
                sb.append(pName).append("=").append(pValue);
            } else {
                sb.append("&").append(pName).append("=").append(pValue);
            }
        }

        return sb.toString();
    }

    private static String checkSignIsValid(
            String sign,
            String timestamp,
            String appId) {

        if (StringUtils.isBlank(sign)) {
            return "签名有误!";
        }

        if (StringUtils.isBlank(timestamp)) {
            return "时间戳不能为空!";
        }

        if (StringUtils.isBlank(appId)) {
            return "appId不能为空!";
        }

        return null;
    }

    /**
     * 增加认证机制的方法
     */
    public static String toSign(Object obj, String devId
            , String devKey) {

        String content = "";

        if (obj instanceof String) {
            content = (String) obj;
        } else {
            if (obj != null) {
                content = JSONObject.toJSONString(obj);
            }
        }

        String toSign = content + "&" + devId + "&" + devKey;

        // 注意 md5 返回小写的32位16进制字符串
        String md5Str = Md5Util.encode(toSign);
        return Base64.encode((md5Str.getBytes()));
    }

    public static boolean isValidRequest(String sign, Object body, String devId, String devKey, Long pushTime, String requestMethod) {

        if ("POST".equals(requestMethod)) {
            return isValidPostRequest(sign, body, devId, devKey, pushTime);
        }

        if ("GET".equals(requestMethod)) {
            return isValidGetRequest(sign, (String) body, devId, devKey, pushTime);
        }

        // todo 其他请求：PUT DELETE
//        return isValidPostRequest(sign, body, devId, devKey, pushTime);
        return true;

    }

    /**
     * POST 请求校验
     *
     * @param sign     签名
     * @param body     body
     * @param devId
     * @param devKey
     * @param pushTime
     * @return
     */
    public static boolean isValidPostRequest(String sign, Object body, String devId, String devKey, Long pushTime) {
        return isValidSign(sign, body, devId, devKey, pushTime);
    }

    /**
     * POST 请求校验
     *
     * @param sign     签名
     * @param url      url
     * @param devId
     * @param devKey
     * @param pushTime
     * @return
     */
    public static boolean isValidGetRequest(String sign, String url, String devId, String devKey, Long pushTime) {

        //fixme  url是否需要解码

        String content = "";
        try {
            url = URLDecoder.decode(url, "UTF-8");
            content = getUrlParams(url);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("签名认证失败：sign={},url={}", sign, url);
        }
        return isValidSign(sign, content, devId, devKey, pushTime);
    }

    /**
     * 校验
     *
     * @param sign     签名
     * @param content  内容
     * @param devId    开发者id
     * @param devKey   开发者key
     * @param pushTime 调用时间戳  秒级别。 System.currentTimeMillis()/1000
     * @return
     */
    private static boolean isValidSign(String sign, Object content, String devId, String devKey, Long pushTime) {

        long l = (System.currentTimeMillis()  - pushTime)/ 1000;

        //校验时间有效性   30s内有效
        if (l >= 30) {
            throw new ServiceException("请求时间超过规定范围时间", StatusCode.NO_DATA.hashCode());
        }

        //1.鉴权
        String sysSign = toSign(content, devId, devKey);
        return sysSign.equals(sign);
    }

    /**
     * GET请求
     * 对url的参数进行加密
     * 例如：url = xxx?a=1&b=2  =>拿a=1&b=2&出来
     *
     * @param url
     * @param devId
     * @param devKey
     * @return
     */
    public static String toSignUrl(String url, String devId, String devKey) {
        String content = "";
        try {
            content = getUrlParams(url);
        } catch (Exception e) {

        }
        return toSign(content, devId, devKey);
    }

    /**
     * 将url参数转换成map
     *
     * @param url www.baidu.com?aa=11&bb=22&cc=33
     * @return
     */
    private static String getUrlParams(String url) throws Exception {

        StringBuffer queryString = new StringBuffer();
        url = url.replace("?", ";");
        if (!url.contains(";")) {
            return url;
        }

        if (url.split(";").length > 0) {
            String[] arr = url.split(";")[1].split("&");

            for (int i = 0; i < arr.length; i++) {
                String[] values = arr[i].split("=");
                String key = values[0];
                String value = values[1];

                queryString.append(key + "=");
                queryString.append(URLEncoder.encode(value, "UTF-8"));
                if (i != (arr.length - 1)) {
                    queryString.append("&");
                }
            }
        }

        return queryString.toString();
    }

    /**
     * 推送时间 秒级别
     *
     * @return
     */
    public static Integer getPushTime() {
        Long now = (System.currentTimeMillis() / 1000);
        return now.intValue();
    }

    public static Integer getTime(Long time) {
        Long now = (time / 1000);
        return now.intValue();
    }


    public static Map<String, String> getGetHeader(String url, String appId, String appKey) {
        String signUrl = SignUtil.toSignUrl(url, appId, appKey);
        return getHeader(signUrl, appId);
    }

    /**
     * @param content body 请求体
     * @return
     */
    public static Map<String, String> getPostHeader(Object content, String appId, String appKey) {
        String sign = SignUtil.toSign(content, appId, appKey);
        return getHeader(sign, appId);
    }

    private static Map<String, String> getHeader(String sign, String appId) {
        Map<String, String> header = new HashMap<String, String>();
        header.put("timestamp", System.currentTimeMillis() / 1000 + "");
        header.put("devId", appId);
        header.put("sign", sign);
        return header;
    }
}
