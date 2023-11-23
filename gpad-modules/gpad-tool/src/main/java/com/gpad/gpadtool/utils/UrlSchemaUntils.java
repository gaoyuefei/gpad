package com.gpad.gpadtool.utils;

import com.alibaba.fastjson2.JSON;
import com.gpad.gpadtool.domain.vo.LoginReqVo;
import com.gpad.gpadtool.domain.vo.LoginResVo;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
@Component
public class UrlSchemaUntils {

    private static String appUsername;

    private static String appPassword;

    @Value("${account.appTokenUsername}")
    public void setAppUsername(String param){
        appUsername = param;
    }

    @Value("${account.appTokenPassword}")
    public void setAppPassword(String param){
        appPassword = param;
    }

    public static LoginResVo getTokenUerName(){
        LoginResVo loginResVo = new LoginResVo();
        try {
//            // 测试用账号密码
            String username = appUsername;
            String password = appPassword;

            // RSA加密
            byte[] usernameEncrytype = RsaUtils.publicEncrytype(username.getBytes(),RsaUtils.string2PublicKey(RsaUtils.getPublicKey()));
            String baseUsername = org.apache.commons.codec.binary.Base64.encodeBase64String(usernameEncrytype);
            // RSA加密
            byte[] passwordEncrytype = RsaUtils.publicEncrytype(password.getBytes(),RsaUtils.string2PublicKey(RsaUtils.getPublicKey()));
            String basePassword = org.apache.commons.codec.binary.Base64.encodeBase64String(passwordEncrytype);

            // 将加密后的账号密码放入对象中(对象类自行创建，参数有username和password即可)
            LoginReqVo vo = new LoginReqVo();
            vo.setUsername(baseUsername);
            vo.setPassword(basePassword);

            //生成随机16位字符串
            String s = RandomUtil.generateRandom(16);
            String str = JSON.toJSONString(vo);

            //AES加密
            SecretKey secretKey = new SecretKeySpec(s.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(1, secretKey);
            byte[] encrypted = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
            String encryptedData = org.apache.commons.codec.binary.Base64.encodeBase64String(encrypted);

            //RSA加密
            byte[] publicEncrytype = RsaUtils.publicEncrytype(s.getBytes(),RsaUtils.string2PublicKey(RsaUtils.getPublicKey()));
            String encryptedKey = Base64.encodeBase64String(publicEncrytype);

            loginResVo.setEncryptedData(encryptedData);
            loginResVo.setEncryptedKey(encryptedKey);

            // TODO 这里需要问清楚过期时间
            System.out.println(encryptedData);
            System.out.println(encryptedKey);
        } catch (Exception e) {
            System.out.println(
                    "报错"
            );
        }
        return loginResVo;
    }
}