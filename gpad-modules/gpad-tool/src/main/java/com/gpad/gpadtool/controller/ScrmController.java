package com.gpad.gpadtool.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.common.core.utils.JwtUtils;
import com.gpad.common.core.web.domain.AjaxResult;
import com.gpad.common.redis.service.RedisService;
import com.gpad.common.security.auth.AuthUtil;
import com.gpad.common.security.service.TokenService;
import com.gpad.common.security.utils.SecurityUtils;
import com.gpad.gpadtool.constant.RedisKey;
import com.gpad.gpadtool.domain.dto.ScrmEncrypeParamVo;
import com.gpad.gpadtool.domain.dto.SyncScrmDeptInfoParamVo;
import com.gpad.gpadtool.domain.dto.SyncScrmInfoResultDto;
import com.gpad.gpadtool.domain.dto.SyncScrmRoleInfoParamVo;
import com.gpad.gpadtool.domain.dto.scrm.*;
import com.gpad.gpadtool.domain.vo.SalesConsultantVo;
import com.gpad.gpadtool.domain.vo.ScanCodeTokenInfoVo;
import com.gpad.gpadtool.domain.vo.SyncScrmUserInfoParamVo;
import com.gpad.gpadtool.repository.UserInfoRepository;
import com.gpad.gpadtool.service.ScrmService;
import com.gpad.gpadtool.utils.CryptoUtils;
import com.gpad.system.api.domain.SysUser;
import com.gpad.system.api.model.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
    private RedisService redisService;

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
     * 获取客户二维码接口
     */
    @Operation(summary = "获取accessToken")
    @PostMapping("/WX/getProductQRcode")
    public R getProductQRcode(@RequestBody ScrmEncrypeParamVo paramVo) {
        log.info("获取客户二维码接口 --->>> paramVo = {}", JSON.toJSONString(paramVo));
        return scrmService.getProductQRcode(paramVo);
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
    @GetMapping("/wx/sit/callback")
    public void sitCallback(@RequestParam("code") String code, @RequestParam("state") String state) throws Exception {
        log.info("企业微信扫码回调--->>> code = {}; state = {}", code, state);
        R<WxUserInfoDto> userInfoByCode = scrmService.getUserInfoByCode(code);
        log.info("企业微信扫码回调---根据Code查企微成员信息 --->>>  {}", JSONObject.toJSONString(userInfoByCode));
        if (userInfoByCode.getCode() != 200) {
            log.error("企业微信扫码回调---根据Code查企微成员信息出错! --->>>  {}", userInfoByCode.getData());
            throw new ServiceException("企业微信扫码回调",500);
        }
        String userCode = userInfoByCode.getData().getUserid();
        log.info("企业微信扫码回调---企微成员信息-userCode === {}", userCode);
        //对接SCRM查用户信息
        ScrmWxCropUserInfoInputDto scrmWxCropUserInfoInputDto = new ScrmWxCropUserInfoInputDto();
        scrmWxCropUserInfoInputDto.setUserId(userCode);
        R<ScrmWxCropUserInfoOutputDto> scrmWxCropUserInfoOutputDtoR = scrmService.getWxCropUserInfo(scrmWxCropUserInfoInputDto);
        log.info("外部接口调用结束method:SCRM返回的用户信息--->>> {}", JSONObject.toJSONString(scrmWxCropUserInfoOutputDtoR));
        String decodeSign = URLDecoder.decode(state, "UTF-8");

        ScanCodeTokenInfoVo scanCodeTokenInfoVo = new ScanCodeTokenInfoVo();
        if (!"200".equals(scrmWxCropUserInfoOutputDtoR.getData().getCode())) {
            scanCodeTokenInfoVo.setMsg(scrmWxCropUserInfoOutputDtoR.getData().getMsg());
            scanCodeTokenInfoVo.setCode(scrmWxCropUserInfoOutputDtoR.getData().getCode());
            log.info("对接SCRM接口返回状态异常--->>> decodeSign={}", JSONObject.toJSONString(decodeSign));
            redisService.setCacheObject(decodeSign, JSON.toJSONString(scanCodeTokenInfoVo), RedisKey.ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
            throw new ServiceException("扫码登录查无账号",500);
        }
        String userId = "";
        String employeeNo = "";
        String dealerCode = "";
        WxCropUserInfoOutputDto wxCropUserInfoOutputDto = scrmWxCropUserInfoOutputDtoR.getData().getData();
        if (ObjectUtil.isNotEmpty(wxCropUserInfoOutputDto)){
            userId = wxCropUserInfoOutputDto.getUserId();
            employeeNo = wxCropUserInfoOutputDto.getEmployeeNo();
            dealerCode = wxCropUserInfoOutputDto.getDeptCode();
            log.info("扫码登录信息结果成功显示userID---》》》{},----employeeNo 》》》{},dealerCode 》》》 {}",userId,employeeNo,dealerCode);
        }
        LoginUser loginUser = new LoginUser();
        SysUser sysUser = new SysUser();
        sysUser.setUserId(System.currentTimeMillis());
        loginUser.setDealerCode(dealerCode);
        sysUser.setUserName(StringUtils.isBlank(employeeNo)?"补偿用户名":employeeNo);
        log.info("扫码登录信息结果userID---》》》{},----employeeNo 》》》{}",userId,employeeNo);
        loginUser.setSysUser(sysUser);
        Map<String, Object> tokenMap = tokenService.createToken(loginUser);
        Object access_token = tokenMap.get("access_token");
        log.info("token为{}",tokenMap);
        log.info("打印key为{}",state);
        log.info("解密后key为{}",decodeSign);
        scanCodeTokenInfoVo.setCode("200");
        scanCodeTokenInfoVo.setExpressTime("60");
        scanCodeTokenInfoVo.setMsg("回调登录成功");
        scanCodeTokenInfoVo.setToken(access_token.toString());
        scanCodeTokenInfoVo.setSalesConsultantVo(new SalesConsultantVo(wxCropUserInfoOutputDto.getName(),wxCropUserInfoOutputDto.getEmployeeNo(),wxCropUserInfoOutputDto.getDeptCode()));
        log.info("打印最后拼接对象{}",JSON.toJSONString(scanCodeTokenInfoVo));
        redisService.setCacheObject(decodeSign, JSON.toJSONString(scanCodeTokenInfoVo), RedisKey.ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
        log.info("回调程序执行结束返回登录令牌");
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
        log.info("PAD端根据订单编号查询PDI接口 开始--->>> {}", JSONObject.toJSONString(scrmPdiFileListInputDto));
        return scrmService.getPdiFileList(scrmPdiFileListInputDto);
    }


    @Operation(summary = "H5端根据订单编号查询PDI接口")
    @PostMapping("/getPdiFileListH5")
    public R<ScrmPdiFileListOutputDto> getPdiFileListH5(@RequestBody ScrmPdiFileListInputDto scrmPdiFileListInputDto) {
        log.info("H5端根据订单编号查询PDI接口 开始--->>> {}", JSONObject.toJSONString(scrmPdiFileListInputDto));
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


    @Operation(summary = "sitH5页面获取token")
    @PostMapping("/getAccessTokenByH5")
    public R sitGetAccessTokenByH5(HttpServletRequest request,@RequestBody ScrmEncrypeParamVo paramVo) {
        log.info("H5页面获取token 开始--->>> {}", JSONObject.toJSONString(paramVo));

        String token = SecurityUtils.getToken(request);
        log.info("记录用户登录token,--->>> {}", token);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("account", paramVo.getData());
        String employeeNo;
        try {
            employeeNo = CryptoUtils.privateKeyDecrypt(paramVo.getData(), privateKey);
        } catch (Exception e) {
            log.info("解密数据报错:  {}", e.getMessage());
            return R.fail("解密数据报错 ");
        }

        log.info("用户登录! employeeNo {}", employeeNo);
        AccountOnLineStatusInputDto accountOnLineStatusInputDto = new AccountOnLineStatusInputDto();
        accountOnLineStatusInputDto.setEmployeeNo(employeeNo);
        R<AccountOnLineStatusOutPutDto> accountOnLineStatusOutPutDtoR = scrmService.accountOnLineStatus(accountOnLineStatusInputDto);
        if (accountOnLineStatusOutPutDtoR.getData().getResultCode().equals("1")) {
            ScrmUserInfoInputDto scrmUserInfoInputDto = new ScrmUserInfoInputDto();
            scrmUserInfoInputDto.setAccount(employeeNo);
            R<ScrmUserInfoToInstrumentOutputDto> infoByAccount = scrmService.getUserInfoByAccount(scrmUserInfoInputDto);
            ScrmUserInfoToInstrumentOutputDto data = infoByAccount.getData();
            String dealerCode = "";
            if (ObjectUtil.isNotEmpty(data)){
                if ("200".equals(data.getCode())){
                    dealerCode = data.getData().getDealerCode();
                }
            }

            LoginUser loginUser = new LoginUser();
            SysUser sysUser = new SysUser();
            sysUser.setUserId(System.currentTimeMillis());
            loginUser.setDealerCode(dealerCode);
            sysUser.setUserName(employeeNo);
//            sysUser.setUserName(dealerCode);
            loginUser.setSysUser(sysUser);
            Map<String, Object> tokenMap = tokenService.createToken(loginUser);
            SalesConsultantVo salesConsultantVo = new SalesConsultantVo();
            if (data!=null&&data.getData()!=null){
                salesConsultantVo.setAccount(data.getData().getAccount());
                salesConsultantVo.setRealName(data.getData().getRealName());
                salesConsultantVo.setDealerCode(data.getData().getDealerCode());
            }
            tokenMap.put("salesConsultantVo",JSONObject.toJSONString(salesConsultantVo));
            log.info("H5页面获取token-结束 --->>> {}", JSONObject.toJSONString(tokenMap));
            return R.ok(tokenMap);
        }else {
            //
            if (com.gpad.common.core.utils.StringUtils.isNotEmpty(token))
            {
                String username = JwtUtils.getUserName(token);
                // 删除用户缓存记录
                AuthUtil.logoutByToken(token);
                log.info("记录用户退出日志,登出成功--->>> {}->>{}", token,username);
            }
            //查不到不作处理
            return R.fail("登录状态已失效，请重新登录");
        }
    }

    @Operation(summary = "获取顾问首页活码")
    @PostMapping("/getConsultantDynamicCode")
    public R<ScrmConsultantCodeOutBO> getConsultantDynamicCode(HttpServletRequest request, @RequestBody ScrmConsultantCodeInputBO scrmConsultantCodeInputBO) {
        String username = SecurityUtils.getUsername();
        scrmConsultantCodeInputBO.setUsername(username);
        return scrmService.getConsultantDynamicCode(scrmConsultantCodeInputBO);
    }
}
