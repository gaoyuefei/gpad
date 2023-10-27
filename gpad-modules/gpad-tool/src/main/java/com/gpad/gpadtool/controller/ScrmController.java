package com.gpad.gpadtool.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.web.domain.AjaxResult;
import com.gpad.common.security.service.TokenService;
import com.gpad.gpadtool.constant.RedisKey;
import com.gpad.gpadtool.domain.dto.*;
import com.gpad.gpadtool.domain.dto.scrm.*;
import com.gpad.gpadtool.domain.vo.SyncScrmUserInfoParamVo;
import com.gpad.gpadtool.repository.UserInfoRepository;
import com.gpad.gpadtool.service.ScrmService;
import com.gpad.gpadtool.utils.CryptoUtils;
import com.gpad.gpadtool.utils.UuidUtil;
import com.gpad.system.api.domain.SysUser;
import com.gpad.system.api.model.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmController.java
 * @Description Scrm登录相关
 * @createTime 2023年09月21日 11:12:00
 */
@Slf4j
@Api(value = "对接企业微信接口API", tags = "对接企业微信接口API")
@RestController
@RequestMapping("/api/scrm")
public class ScrmController {
    @Autowired
    private ScrmService scrmService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RestTemplate restTemplate;


    @Value("${scrm.publicKey}")
    private String publicKey;
    @Value("${scrm.privateKey}")
    private String privateKey;


    /**
     * 获取accessToken接口
     */
    @Operation(summary = "获取accessToken")
    @GetMapping("/wx/getAccessToken")
    public AjaxResult getAccessToken() {
        return scrmService.getAccessToken();
    }

    /**
     * 生成企业微信登录二维码
     */
    @Operation(summary = "生成企业微信登录二维码")
    @PostMapping("/wx/getQrCode")
    public AjaxResult getQrCode(@RequestParam("sign") String sign) throws Exception {
        log.info("生成企业微信登录二维码 --->>> sign = {}", sign);
        return scrmService.getQrCode(sign);
    }

    /**
     * 企业微信扫码回调
     */
    @Operation(summary = "企业微信扫码回调")
    @GetMapping("/wx/callback")
    public R callback(@RequestParam("code") String code, @RequestParam("state") String state) throws Exception {
        log.info("企业微信扫码回调--->>> code = {}; state = {}", code, state);
        R<WxUserInfoDto> userInfoByCode = scrmService.getUserInfoByCode(code);
        log.info("企业微信扫码回调---getUserInfoByCode --->>>  {}", JSONObject.toJSONString(userInfoByCode));
        if (userInfoByCode.getCode() != 200) {
            log.error("企业微信扫码回调---getUserInfoByCode出错! --->>>  {}", userInfoByCode.getData());
            return R.fail("企业微信扫码回调失败");
        }
//        authService.login(userInfoByCode.getData().getUserid(), state);
        String userCode = userInfoByCode.getData().getUserid();
        String sign = state;
        log.info("用户登录! userCode {};sign {}", userCode, sign);
        //用userCode查SCRM用户表
        //UserInfoDto userInfoDto = userInfoRepository.findDtoByAccount(userCode);
        ScrmWxCropUserInfoInputDto scrmWxCropUserInfoInputDto = new ScrmWxCropUserInfoInputDto();
        scrmWxCropUserInfoInputDto.setUserId(userCode);
        R<ScrmWxCropUserInfoOutputDto> scrmWxCropUserInfoOutputDtoR = scrmService.getWxCropUserInfo(scrmWxCropUserInfoInputDto);
        if (!scrmWxCropUserInfoOutputDtoR.getData().getCode().equals("200")) {
            return R.fail("企业微信扫码登录获取企微成员信失败");
        }
        ScrmUserInfoInputDto scrmUserInfoInputDto = new ScrmUserInfoInputDto();
        scrmUserInfoInputDto.setAccount(scrmWxCropUserInfoOutputDtoR.getData().getData().getEmployeeNo());
        R<ScrmUserInfoToInstrumentOutputDto> scrmUserInfoToInstrumentOutputDtoR = scrmService.getUserInfoByAccount(scrmUserInfoInputDto);
        if (!scrmUserInfoToInstrumentOutputDtoR.getData().getCode().equals("200")) {
            return R.fail("企业微信扫码登录获取SCRM员工信息失败");
        }
        ScrmUserInfoToInstrumentOutputDto scrmUserInfoToInstrumentOutputDto = scrmUserInfoToInstrumentOutputDtoR.getData();
        if (scrmUserInfoToInstrumentOutputDto.getData() != null) {
            LoginUser loginUser = new LoginUser();
            SysUser sysUser = new SysUser();
            sysUser.setUserId(scrmUserInfoToInstrumentOutputDto.getData().getId());
            sysUser.setUserName(scrmUserInfoToInstrumentOutputDto.getData().getRealName());
            loginUser.setSysUser(sysUser);
            Map<String, Object> tokenMap = tokenService.createToken(loginUser);
            return R.ok(tokenMap);

        }
        //查不到不作处理
        return R.fail("企业微信扫码回调失败");
    }

    /**
     * 根据Code获取用户信息
     */
    @Operation(summary = "根据Code获取用户信息")
    @GetMapping("/wx/getUserInfoByCode")
    public R<WxUserInfoDto> getUserInfoByCode(@RequestParam("code") String code) throws Exception {
        log.info("根据Code获取用户信息 --->>> code = {}", code);
        return scrmService.getUserInfoByCode(code);
    }

    /**
     * 同步用户信息接口
     */
    @Operation(summary = "同步用户信息接口")
    @PostMapping("/syncUserInfo")
    public SyncScrmInfoResultDto syncUserInfo(@RequestBody ScrmEncrypeParamVo paramVo) {
        log.info("SCRM同步用户信息 --->>> {}", JSONObject.toJSONString(paramVo));
        String decodeStr;
        try {
            decodeStr = CryptoUtils.privateKeyDecrypt(paramVo.getData(), privateKey);
        } catch (Exception e) {
            log.info("解密数据报错:  {}", e.getMessage());
            return new SyncScrmInfoResultDto(500, "解密数据报错 " + e.getMessage(), null);
        }
        SyncScrmUserInfoParamVo param = JSONObject.parseObject(decodeStr, SyncScrmUserInfoParamVo.class);
        return scrmService.syncScrmUserInfo(param);
    }

    /**
     * 同步角色信息接口
     */
    @Operation(summary = "同步角色信息接口")
    @PostMapping("/syncRoleInfo")
    public SyncScrmInfoResultDto syncRoleInfo(@RequestBody ScrmEncrypeParamVo paramVo) {
        log.info("SCRM同步角色信息接口 --->>> {}", JSONObject.toJSONString(paramVo));
        String decodeStr;
        try {
            decodeStr = CryptoUtils.privateKeyDecrypt(paramVo.getData(), privateKey);
        } catch (Exception e) {
            log.info("解密数据报错:  {}", e.getMessage());
            return new SyncScrmInfoResultDto(500, "解密数据报错 " + e.getMessage(), null);
        }
        SyncScrmRoleInfoParamVo param = JSONObject.parseObject(decodeStr, SyncScrmRoleInfoParamVo.class);
        return scrmService.syncScrmRoleInfo(param);
    }

    /**
     * 同步部门信息接口
     */
    @Operation(summary = "同步部门信息接口")
    @PostMapping("/syncDeptInfo")
    public SyncScrmInfoResultDto syncDeptInfo(@RequestBody ScrmEncrypeParamVo paramVo) {
        log.info("同步部门信息接口 --->>> {}", JSONObject.toJSONString(paramVo));
        String decodeStr;
        try {
            decodeStr = CryptoUtils.privateKeyDecrypt(paramVo.getData(), privateKey);
        } catch (Exception e) {
            log.info("解密数据报错:  {}", e.getMessage());
            return new SyncScrmInfoResultDto(500, "解密数据报错 " + e.getMessage(), null);
        }
        SyncScrmDeptInfoParamVo param = JSONObject.parseObject(decodeStr, SyncScrmDeptInfoParamVo.class);
        return scrmService.syncScrmDeptInfo(param);
    }


    @Operation(summary = "加密用户信息")
    @PostMapping("/encodeUser")
    public String encodeUser(@RequestBody SyncScrmUserInfoParamVo param) {
        String encryptData = "";
        try {
            encryptData = CryptoUtils.publicKeyEncrypt(JSON.toJSONString(param), publicKey);
        } catch (Exception e) {
            log.info("加密数据报错: {}", e.getMessage());
        }
        return encryptData;
    }


    @Operation(summary = "加密角色信息")
    @PostMapping("/encodeRole")
    public String encodeRole(@RequestBody SyncScrmRoleInfoParamVo param) {
        String encryptData = "";
        try {
            encryptData = CryptoUtils.publicKeyEncrypt(JSON.toJSONString(param), publicKey);
        } catch (Exception e) {
            log.info("加密数据报错: {}", e.getMessage());
        }
        return encryptData;
    }


    @Operation(summary = "加密部门信息")
    @PostMapping("/encodeDept")
    public String encodeDept(@RequestBody SyncScrmDeptInfoParamVo param) {
        String encryptData = "";
        try {
            encryptData = CryptoUtils.publicKeyEncrypt(JSON.toJSONString(param), publicKey);
        } catch (Exception e) {
            log.info("加密数据报错: {}", e.getMessage());
        }
        return encryptData;
    }

    @Operation(summary = "加密")
    @PostMapping("/encode")
    public String encode(@RequestBody Map<String, String> param) {

        // 加密字符串
        JSONObject jsonObject = new JSONObject();
        String encryptData = "";
        try {
            encryptData = CryptoUtils.publicKeyEncrypt(JSON.toJSONString(param), publicKey);
        } catch (Exception e) {
            log.info("加密数据报错: {}", e.getMessage());
        }
        jsonObject.put("data", encryptData);
        String json = JSONObject.toJSONString(jsonObject);
        log.info("加密后的数据:  {}", json);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //封装成一个请求对象
        HttpEntity request = new HttpEntity(json, headers);
        String url = "http://localhost:9444/api/decode";
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    @Operation(summary = "解密")
    @PostMapping("/decode")
    public Map<String, Object> decode(@RequestBody String data) {
        // 解密接收的加密字符串
        Map dataMap = JSON.parseObject(data);
        String dataStr = String.valueOf(dataMap.get("data"));
        String decryptData = "";
        try {
            decryptData = CryptoUtils.privateKeyDecrypt(dataStr, privateKey);
        } catch (Exception e) {
            log.info("解密数据报错");
        }
        return JSON.parseObject(decryptData);
    }

    @Operation(summary = "账号登录状态验证接口")
    @PostMapping("/accountOnLineStatus")
    public R<AccountOnLineStatusOutPutDto> accountOnLineStatus(@RequestBody AccountOnLineStatusInputDto accountOnLineStatusInputDto) {
        return scrmService.accountOnLineStatus(accountOnLineStatusInputDto);
    }

    @Operation(summary = "根据订单编号查询PDI接口")
    @PostMapping("/getPdiFileList")
    public R<ScrmPdiFileListOutputDto> getPdiFileList(@RequestBody ScrmPdiFileListInputDto scrmPdiFileListInputDto) {
        return scrmService.getPdiFileList(scrmPdiFileListInputDto);
    }

    @Operation(summary = "获取schema")
    @PostMapping("/getPdiSchema")
    public R<ScrmPdiFileListOutputDto> getPdiSchema(@RequestBody ScrmPdiFileListInputDto scrmPdiFileListInputDto) {
        return scrmService.getPdiSchema(scrmPdiFileListInputDto);
    }

    @Operation(summary = "数据获取请求")
    @PostMapping("/getBasicData")
    public R<ScrmBasicDataOutputDto> getBasicData(@RequestBody ScrmBasicDataInputDto scrmBasicDataInputDto) {
        return scrmService.getBasicData(scrmBasicDataInputDto);
    }

    @Operation(summary = "SCRM员工信息")
    @PostMapping("/getUserInfoToInstrument")
    public R<ScrmUserInfoToInstrumentOutputDto> getUserInfoToInstrument(@RequestBody ScrmUserInfoInputDto scrmUserInfoInputDto) {
        return scrmService.getUserInfoByAccount(scrmUserInfoInputDto);
    }

    @Operation(summary = "获取企微成员信息接口")
    @PostMapping("/getWxCropUserInfo")
    public R<ScrmWxCropUserInfoOutputDto> getWxCropUserInfo(@RequestBody ScrmWxCropUserInfoInputDto scrmWxCropUserInfoInputDto) {
        return scrmService.getWxCropUserInfo(scrmWxCropUserInfoInputDto);
    }

    @Operation(summary = "H5页面获取token")
    @PostMapping("/getAccessTokenByH5")
    public R getAccessTokenByH5(@RequestBody ScrmEncrypeParamVo paramVo) {
        log.info("H5页面获取token --->>> {}", JSONObject.toJSONString(paramVo));
        String employeeNo;
        try {
              employeeNo = CryptoUtils.publicKeyEncrypt(paramVo.getData(), publicKey);
        } catch (Exception e) {
            log.info("解密数据报错:  {}", e.getMessage());
            return R.fail("解密数据报错 ");
        }

        log.info("用户登录! employeeNo {}", employeeNo);
        AccountOnLineStatusInputDto accountOnLineStatusInputDto = new AccountOnLineStatusInputDto();
        accountOnLineStatusInputDto.setEmployeeNo(employeeNo);
        R<AccountOnLineStatusOutPutDto> accountOnLineStatusOutPutDtoR = scrmService.accountOnLineStatus(accountOnLineStatusInputDto);
        if (accountOnLineStatusOutPutDtoR.getData().getResultCode().equals("1")) {
            //用userCode查SCRM用户表
            ScrmWxCropUserInfoInputDto scrmWxCropUserInfoInputDto = new ScrmWxCropUserInfoInputDto();
            scrmWxCropUserInfoInputDto.setUserId(employeeNo);
            R<ScrmWxCropUserInfoOutputDto> scrmWxCropUserInfoOutputDtoR = scrmService.getWxCropUserInfo(scrmWxCropUserInfoInputDto);
            if (!scrmWxCropUserInfoOutputDtoR.getData().getCode().equals("200")) {
                return R.fail("企业微信扫码登录获取企微成员信失败");
            }
            ScrmUserInfoInputDto scrmUserInfoInputDto = new ScrmUserInfoInputDto();
            scrmUserInfoInputDto.setAccount(scrmWxCropUserInfoOutputDtoR.getData().getData().getEmployeeNo());
            R<ScrmUserInfoToInstrumentOutputDto> scrmUserInfoToInstrumentOutputDtoR = scrmService.getUserInfoByAccount(scrmUserInfoInputDto);
            if (!scrmUserInfoToInstrumentOutputDtoR.getData().getCode().equals("200")) {
                return R.fail("企业微信扫码登录获取SCRM员工信息失败");
            }
            ScrmUserInfoToInstrumentOutputDto scrmUserInfoToInstrumentOutputDto = scrmUserInfoToInstrumentOutputDtoR.getData();
            if (scrmUserInfoToInstrumentOutputDto.getData() != null) {
                LoginUser loginUser = new LoginUser();
                SysUser sysUser = new SysUser();
                sysUser.setUserId(scrmUserInfoToInstrumentOutputDto.getData().getId());
                sysUser.setUserName(scrmUserInfoToInstrumentOutputDto.getData().getRealName());
                loginUser.setSysUser(sysUser);
                Map<String, Object> tokenMap = tokenService.createToken(loginUser);
                return R.ok(tokenMap);

            }
            //查不到不作处理
            return R.fail("企业微信扫码回调失败");
        }
        return R.fail("非法登录");
    }
}
