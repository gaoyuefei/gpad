package com.gpad.gpadtool.controller.autoSignature;

import com.alibaba.fastjson.JSON;
import com.gpad.common.core.bo.input.AutoSignatureGetLinkInputBO;
import com.gpad.common.core.bo.input.AutoSignatureInputBO;
import com.gpad.common.core.bo.input.ContinueStartSignatureInputBO;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.service.AutoSignatureService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @BelongsProject: gpad-api
 * @BelongsPackage: com.gpad.gpadtool.controller.autoSignature
 * @Author: LF
 * @CreateTime: 2023-09-21
 * @Description: 君子签证
 * @Version: 1.0
 */
@RestController
@RequestMapping("/gentlemanSignature")
public class GentlemanSignatureController {

    @Resource
    AutoSignatureService  autoSignatureService;

    /**
     * 发起裙子签证
     */
    @Operation(summary = "发起裙子签证")
    @PostMapping("/v2/sign/applySign")
    public R<String> startGentlemanSignature(@RequestParam(value = "autoSignatureInputBOForm",required = false) String autoSignatureInputBOForm,
                                             @RequestParam(value = "file",required = false) MultipartFile file,
                                             @RequestParam(value = "fileCustomerPng",required = false) MultipartFile fileCustomerPng,
                                             @RequestParam(value = "fileProductPng",required = false) MultipartFile fileProductPng
    ){
        System.out.println(autoSignatureInputBOForm);
        AutoSignatureInputBO autoSignatureInputBO = JSON.parseObject(autoSignatureInputBOForm, AutoSignatureInputBO.class);
        return autoSignatureService.startGentlemanSignature(autoSignatureInputBO,file,fileCustomerPng,fileProductPng);
    }

//    public static void main(String[] args) {
//        JSONArray req = new JSONArray();
//        AutoSignatureInputBO autoSignatureInputBO = new AutoSignatureInputBO();
//        autoSignatureInputBO.setFullName("1");
//        autoSignatureInputBO.setIdentityType(1);
//        System.out.println(JSONObject.toJSONString(autoSignatureInputBO));
//        String str = JSONObject.toJSONString(autoSignatureInputBO);
//        req.add(str);
//        AutoSignatureInputBO autoSignatureInputBO1 = new AutoSignatureInputBO();
//        autoSignatureInputBO1.setFullName("2");
//        autoSignatureInputBO1.setIdentityType(3);
//        autoSignatureInputBO1.setIdentityCard("21615616156");
//        String str1 = JSONObject.toJSONString(autoSignatureInputBO1);
//        req.add(str1);
//        String ooo = JSONArray.toJSONString(req);
//        System.out.println(ooo);
//    }

    /**
     * 获取裙子合同
     */
    @Operation(summary = "获取裙子合同连接")
    @PostMapping("/v2/sign/link")
    public R<String> getStartGentlemanSignatureLink(@RequestBody AutoSignatureGetLinkInputBO autoSignatureGetLinkInputBO){
        return autoSignatureService.getStartGentlemanSignatureLink(autoSignatureGetLinkInputBO);
    }


    /**
     * 签约发起（追加签署人）
     */
    @Operation(summary = "签约发起（追加签署人）")
    @PostMapping("/v2/sign/signatory/add")
    public R continueStartGenManSignature(@RequestBody ContinueStartSignatureInputBO continueStartSignatureInputBO){
        return autoSignatureService.continueStartGenManSignature(continueStartSignatureInputBO);
    }


    /**
     * 企业实名认证状态查询
     */
    @Operation(summary = "企业实名认证状态查询")
    @PostMapping("/v2/sign/signatory/add1")
    public R checkRealNameAuthSecurity(@RequestBody ContinueStartSignatureInputBO continueStartSignatureInputBO){
        return null;
    }

    /**
     * 企业实名认证上传
     */
    @Operation(summary = "企业实名认证上传")
    @PostMapping("/v2/sign/signatory/add2")
    public R uploadRealBusinessNameAuth(@RequestBody ContinueStartSignatureInputBO continueStartSignatureInputBO){
        return null;
    }

    /**
     * 企业实名认证重传
     */
    @Operation(summary = "企业实名认证重传")
    @PostMapping("/v2/sign/signatory/add3")
    public R againUploadRealBusinessNameAuth(@RequestBody ContinueStartSignatureInputBO continueStartSignatureInputBO){
        return null;
    }

    /**
     * 企业实名认证重传
     */
    @Operation(summary = "二要数认证接口")
    @PostMapping("/v2/auth/userValid")
    public R authUserValid(@RequestBody AutoSignatureInputBO autoSignatureInputBO){
        return autoSignatureService.authUserValid(autoSignatureInputBO);
    }

//    /**
//     * 上传销售人员签名图片
//     */
//    @Operation(summary = "上传销售人员签名图片")
//    @PostMapping("/v2/auth/userValid")
//    public R authUserValid(@RequestBody AutoSignatureInputBO autoSignatureInputBO){
//        return autoSignatureService.authUserValid(autoSignatureInputBO);
//    }

    /**
     * 发起线下签署
     */
    @Operation(summary = "发起线下签署")
    @GetMapping("/turn/off/signature")
    public R turnOffSignature(@RequestParam("status") String status , @RequestParam("bussinessNo") String bussinessNo){
        return autoSignatureService.turnOffSignature(status,bussinessNo);
    }


    /**
     * 发起线下签署
     */
    @Operation(summary = "发起线下签署")
    @GetMapping("/turn/off/signature132132132")
    public R turnOffSignature(@RequestBody AutoSignatureInputBO autoSignatureInputBO){
        return null;
    }

}
