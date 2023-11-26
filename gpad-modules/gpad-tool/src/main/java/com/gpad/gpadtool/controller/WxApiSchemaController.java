package com.gpad.gpadtool.controller;

import com.alibaba.fastjson2.JSON;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.gpadtool.domain.dto.wxapi.ExhibitionMixPadInputBO;
import com.gpad.gpadtool.domain.dto.wxapi.WxApiCommentInputBO;
import com.gpad.gpadtool.domain.vo.OrderCommentUrlVo;
import com.gpad.gpadtool.service.WxApiSchemaService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


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
        String decodeSign = null;
        try {
             decodeSign = URLDecoder.decode(wxApiSchemaUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(decodeSign)){
            return R.fail("非法参数");
        }
        return wxApiSchemaService.getgetSkipSchemaUrl(decodeSign);
    }

    /**
     * wx获取获取评论跳转连接
     */
    @Operation(summary = "wx获取获取评论跳转连接")
    @PostMapping("/thirdparty-app/scrm/getOrderCommentSkipUrl")
    public R getOrderCommentUrl(@RequestBody OrderCommentUrlVo OrderCommentUrlVo){
        log.info("-getOrderComment入参-->>> getOrderComment = {}", OrderCommentUrlVo);
        return wxApiSchemaService.getOrderCommentUrl(OrderCommentUrlVo);
    }

    /**
     * 获取urlSchema （暂时废弃）
     */
    @Operation(summary = "获取urlSchema")
    @GetMapping("/sit/thirdparty-app/urlSchema")
    public R sitUrlSchema(@RequestParam("wxApiSchemaUrl") String wxApiSchemaUrl){
        String decodeSign = null;
        try {
            decodeSign = URLDecoder.decode(wxApiSchemaUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(decodeSign)){
            return R.fail("非法参数");
        }
        log.info("获取urlSchema --->>> wxApiSchemaUrl = {}", decodeSign);
        return wxApiSchemaService.sitUrlSchema(decodeSign);
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

}

