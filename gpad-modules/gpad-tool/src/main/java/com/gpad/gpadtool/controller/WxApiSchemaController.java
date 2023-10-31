package com.gpad.gpadtool.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.BaseGrtResultDto;
import com.gpad.gpadtool.domain.dto.OrderDeliverDateParamVo;
import com.gpad.gpadtool.domain.dto.UserInfoDto;
import com.gpad.gpadtool.domain.vo.LoginReqVo;
import com.gpad.gpadtool.domain.vo.LoginResVo;
import com.gpad.gpadtool.service.WxApiSchemaService;
import com.gpad.gpadtool.utils.*;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @BelongsProject: gpad-api
 * @BelongsPackage: com.gpad.gpadtool.controller.wxapiSchem
 * @Author: LF
 * @CreateTime: 2023-10-16
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/wxApiSchema")
@Api(value = "获取微信小程序跳转相关", tags = "获取微信小程序跳转相关")
public class WxApiSchemaController {

    @Autowired
    private WxApiSchemaService wxApiSchemaService;

    /**
     * 获取urlSchema
     */
    @Operation(summary = "获取urlSchema")
    @GetMapping("/thirdparty-app/urlSchema")
    public R urlSchema(@RequestParam("wxApiSchemaUrl") String wxApiSchemaUrl){
        log.info("获取urlSchema --->>> wxApiSchemaUrl = {}", wxApiSchemaUrl);
        return wxApiSchemaService.getgetSkipSchemaUrl(wxApiSchemaUrl);
    }




//    public static void main(String[] args) {
//        //加密方法
//        try {
//            // 测试用账号密码
//            String username = "ceshi01";
//            String password = "Gac@2020";
//
//            // RSA加密
//            byte[] usernameEncrytype = RsaUtils.publicEncrytype(username.getBytes(),RsaUtils.string2PublicKey(RsaUtils.getPublicKey()));
//            String baseUsername = Base64.encodeBase64String(usernameEncrytype);
//            // RSA加密
//            byte[] passwordEncrytype = RsaUtils.publicEncrytype(password.getBytes(),RsaUtils.string2PublicKey(RsaUtils.getPublicKey()));
//            String basePassword = Base64.encodeBase64String(passwordEncrytype);
//
//            // 将加密后的账号密码放入对象中(对象类自行创建，参数有username和password即可)
//            LoginReqVo vo = new LoginReqVo();
//            vo.setUsername(baseUsername);
//            vo.setPassword(basePassword);
//
//            //生成随机16位字符串
//            String s = RandomUtil.generateRandom(16);
//            String str = JSON.toJSONString(vo);
//
//            //AES加密
//            SecretKey secretKey = new SecretKeySpec(s.getBytes(), "AES");
//            Cipher cipher = Cipher.getInstance("AES");
//            cipher.init(1, secretKey);
//            byte[] encrypted = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
//            String encryptedData = Base64.encodeBase64String(encrypted);
//
//            //RSA加密
//            byte[] publicEncrytype = RsaUtils.publicEncrytype(s.getBytes(),RsaUtils.string2PublicKey(RsaUtils.getPublicKey()));
//            String encryptedKey = Base64.encodeBase64String(publicEncrytype);
//
//            //这两个参数作为请求参数
//            System.out.println(encryptedData);
//            System.out.println(encryptedKey);
//        } catch (Exception e) {
//            System.out.println(
//                    "报错"
//            );
//        }
//    }

}

