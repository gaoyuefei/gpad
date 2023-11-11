package com.gpad.gpadtool.controller;

import com.alibaba.fastjson2.JSON;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.wxapi.ExhibitionMixPadInputBO;
import com.gpad.gpadtool.domain.dto.wxapi.WxApiCommentInputBO;
import com.gpad.gpadtool.domain.vo.OrderCommentUrlVo;
import com.gpad.gpadtool.service.WxApiSchemaService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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


    /**
     * wx获取获取评论跳转连接
     */
    @Operation(summary = "wx获取获取评论跳转连接")
    @PostMapping("/thirdparty-app/scrm/getOrderCommentSkipUrl")
    public R getOrderCommentUrl(@RequestBody OrderCommentUrlVo OrderCommentUrlVo){
        log.info(" -getOrderComment入参-->>> getOrderComment = {}", OrderCommentUrlVo);
        return wxApiSchemaService.getOrderCommentUrl(OrderCommentUrlVo);
    }


    /**
     * 获取urlSchema （暂时废弃）
     */
    @Operation(summary = "获取urlSchema")
    @GetMapping("/sit/thirdparty-app/urlSchema")
    public R sitUrlSchema(@RequestParam("wxApiSchemaUrl") String wxApiSchemaUrl){
        log.info("获取urlSchema --->>> wxApiSchemaUrl = {}", wxApiSchemaUrl);
        return wxApiSchemaService.sitUrlSchema(wxApiSchemaUrl);
    }

    /**
     * wx获取的是否评论及评论内容
     */
    @Operation(summary = "获取urlSchema")
    @PostMapping("/thirdparty-app/scrm/getOrderComment")
    public R getOrderComment(@RequestBody WxApiCommentInputBO wxApiCommentInputBO){
        log.info(" -getOrderComment入参-->>> getOrderComment = {}", JSON.toJSONString(wxApiCommentInputBO));
        return wxApiSchemaService.getOrderComment(wxApiCommentInputBO);
    }

    /**
     * 获取素材连接
     */
    @Operation(summary = "获取素材连接")
    @PostMapping("/exhibition/queryExhibitionMixPad")
    public R queryExhibitionMixPad(@RequestBody ExhibitionMixPadInputBO exhibitionMixPadInputBO){
        log.info(" -queryExhibitionMixPad入参-->>> queryExhibitionMixPad = {}", JSON.toJSONString(exhibitionMixPadInputBO));
        return wxApiSchemaService.queryExhibitionMixPad(exhibitionMixPadInputBO);
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

