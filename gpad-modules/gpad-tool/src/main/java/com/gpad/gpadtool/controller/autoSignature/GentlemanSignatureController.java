package com.gpad.gpadtool.controller.autoSignature;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.gpad.common.core.bo.input.*;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.common.security.utils.SecurityUtils;
import com.gpad.gpadtool.constant.CommCode;
import com.gpad.gpadtool.domain.dto.UploadFileOutputDto;
import com.gpad.gpadtool.service.AutoSignatureService;
import com.gpad.gpadtool.utils.DateUtil;
import com.gpad.gpadtool.utils.FileUtil;
import com.gpad.gpadtool.utils.MonitorUtil;
import com.gpad.gpadtool.utils.UuidUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @BelongsProject: gpad-api
 * @BelongsPackage: com.gpad.gpadtool.controller.autoSignature
 * @Author: LF
 * @CreateTime: 2023-09-21
 * @Description: 君子签证
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/gentlemanSignature")
public class GentlemanSignatureController {

    @Value("${config.file.path}")
    private String FILE_PATH;

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

        //TODO  校验 直接返回避免资源消耗
        if (null == fileProductPng && null == fileCustomerPng){
            return R.ok(null,"专家签名成功,等待用户签名");
        }

        log.info("发起裙子签证进入1 method：startGentlemanSignature--->>> {}",JSON.toJSONString(autoSignatureInputBOForm));
        AutoSignatureInputBO autoSignatureInputBO = JSON.parseObject(autoSignatureInputBOForm, AutoSignatureInputBO.class);
        // TODO 客户测试临时应对
        if (ObjectUtil.isEmpty(autoSignatureInputBO)){
            throw new ServiceException("客户信息有误，请检查客户信息", CommCode.DATA_UPDATE_WRONG.getCode());
        }
        if (StringUtils.isEmpty(autoSignatureInputBO.getIdentityCard())){
            throw new ServiceException("客户身份信息有误，请检查身份信息", CommCode.DATA_UPDATE_WRONG.getCode());
        }
        if (StringUtils.isEmpty(autoSignatureInputBO.getFullName())){
            throw new ServiceException("客户姓名有误，请检查姓名信息", CommCode.DATA_UPDATE_WRONG.getCode());
        }
//        if (StringUtils.isEmpty(autoSignatureInputBO.getMobile())){
//            throw new ServiceException("客户手机号码有误，请检查手机号码信息", CommCode.DATA_UPDATE_WRONG.getCode());
//        }
        String username = SecurityUtils.getUsername();
        autoSignatureInputBO.setAccount(username);
        return autoSignatureService.startGentlemanSignature(autoSignatureInputBO,file,fileCustomerPng,fileProductPng);
    }

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
     * 产品
     */
    @Operation(summary = "二要数认证接口")
    @PostMapping("/v2/auth/accountValid")
    public R authUserSignatureValid(@RequestBody AuthUserSignatureInputBO authUserSignatureInputBO){
        return autoSignatureService.authUserSignatureValid(authUserSignatureInputBO);
    }

    /**
     * 客户
     */
    @Operation(summary = "二要数认证接口")
    @PostMapping("/v2/auth/userValid")
    public R authUserSignatureValid(@RequestBody AutoSignatureUserInputBO autoSignatureUserInputBO){
        return autoSignatureService.authUserValid(autoSignatureUserInputBO);
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
    @PostMapping("/turn/off/signature")
    public R turnOffSignature(@RequestBody SignatureTurnOffSignInputBO SignatureTurnOffSignInputBO){
        return autoSignatureService.turnOffSignature(SignatureTurnOffSignInputBO);
    }


    /**
     * 君子签个人签名上传
     */
    @Operation(summary = "君子签个人签名上传")
    @PostMapping("/Signature/uploadFile")
    public R<UploadFileOutputDto> signatureUploadFile(@RequestParam(value = "fileProductPng") MultipartFile fileProductPng,
                                                      @RequestParam(value = "suffix") String suffix,
                                                      @RequestParam(value = "authUserSignatureInputBO", required = false) String authUserSignatureInputBO) {
        AuthUserSignatureInputBO productSignature = JSON.parseObject(authUserSignatureInputBO, AuthUserSignatureInputBO.class);
        MonitorUtil.start();
        Boolean fileType = FileUtil.getFileType(suffix);
        if (!fileType) {
            return R.fail("文件类型为空!");
        }
        //文件存储路径
        String filePath = FILE_PATH.concat(File.separator).concat(DateUtil.generateDateTimeStr()).concat(File.separator);
        log.info("文件上传!当前文件存储路径=== {}", filePath);
        String newFilename = UuidUtil.generateUuidWithDate() + "." + suffix;
        FileUtil.uploadFile(fileProductPng, filePath, newFilename);

        String result = filePath.concat(newFilename).replaceAll("\\\\", "/");
        String subResult = result.substring(4);
        UploadFileOutputDto uploadFileOutputDto = new UploadFileOutputDto();
        uploadFileOutputDto.setFileName(newFilename);
        uploadFileOutputDto.setFilePath(subResult);
        log.info("文件上传!结束返回接口参数为 {}", JSON.toJSONString(uploadFileOutputDto));
        autoSignatureService.signatureUploadFile(productSignature,fileProductPng,subResult);
        MonitorUtil.finish("uploadFile");
        return R.ok(uploadFileOutputDto);
    }



   /**
     * 获取签约合同文件流
     */
    @Operation(summary = "上传销售人员签名图片")
    @PostMapping("/v2/auth/filtOUTSteam")
    public R filtOUTSteam(HttpServletResponse response, @RequestParam(value = "fileUrl") String url){
        return autoSignatureService.filtOUTSteam(url,response);
    }

}

