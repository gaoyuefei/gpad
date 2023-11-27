package com.gpad.gpadtool.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.UuidUtils;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.common.core.web.domain.AjaxResult;
import com.gpad.common.redis.service.RedisService;
import com.gpad.gpadtool.constant.CommCode;
import com.gpad.gpadtool.constant.RedisKey;
import com.gpad.gpadtool.domain.dto.*;
import com.gpad.gpadtool.domain.dto.scrm.*;
import com.gpad.gpadtool.domain.vo.ScrmConsultantCodeVo;
import com.gpad.gpadtool.domain.vo.SyncScrmUserInfoParamVo;
import com.gpad.gpadtool.enums.ScrmToPadFileTypeEnum;
import com.gpad.gpadtool.repository.DeptInfoRepository;
import com.gpad.gpadtool.repository.RoleInfoRepository;
import com.gpad.gpadtool.repository.UserInfoRepository;
import com.gpad.gpadtool.utils.CryptoUtils;
import com.gpad.gpadtool.utils.DateUtil;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.gpad.common.core.web.domain.AjaxResult.*;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmService.java
 * @Description Scrm逻辑代码
 * @createTime 2023年09月21日 16:56:02
 */
@Service
@RefreshScope
public class ScrmService {
    @Value("${wx.appId}")
    private String appId;
    @Value("${wx.agentId}")
    private String agentId;
    @Value("${wx.secret}")
    private String secret;
    @Value("${wx.url.accessToken}")
    private String accessTokenUrl;
    @Value("${wx.url.qrCode}")
    private String qrCodeUrl;
    @Value("${wx.url.userInfoByCode}")
    private String userInfoByCode;
    @Value("${wx.url.redirect-url}")
    private String redirectUrl;

    @Value("${scrm.publicKey}")
    private String publicKey;
    @Value("${scrm.privateKey}")
    private String privateKey;

    @Value("${scrm.basicDataPublicKey}")
    private String basicDataPublicKey;

    @Value("${scrm.publicInterfaceKey}")
    private String publicInterfaceKey;

    @Value("${scrm.appUrl}")
    private String appUrl;

    @Value("${scrm.url}")
    private String scrmUrl;
    @Value("${scrm.accountOnLineStatus}")
    private String accountOnLineStatus;
    @Value("${scrm.getPdiFileList}")
    private String getPdiFileList;
    @Value("${scrm.getBasicData}")
    private String getBasicData;
    @Value("${scrm.getUserInfoByAccount}")
    private String getUserInfoByAccount;
    @Value("${scrm.getWxCropUserInfo}")
    private String getWxCropUserInfo;

    @Value("${scrm.getProductQRcode}")
    private String getProductQRcode;

    @Value("${scrm.qcodeAuth}")
    private String qcodeAuth;

    private static final Logger log = LoggerFactory.getLogger(ScrmService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RoleInfoRepository roleInfoRepository;

    @Autowired
    private DeptInfoRepository deptInfoRepository;


    public AjaxResult getAccessToken() {
        log.info("getAccessToken--->>> {}", System.currentTimeMillis());
        //从缓存取，缓存取不到才查接口
        if(null != redisService.getCacheObject(RedisKey.ACCESS_TOKEN_KEY)){
            String accessToken = redisService.getCacheObject(RedisKey.ACCESS_TOKEN_KEY).toString();
            log.info("accessToken==={}", accessToken);
            if (Strings.isNotEmpty(accessToken)) {
                log.info("从redis缓存获取accessToken! ---> {}", accessToken);
                return success(accessToken);
            }
        }

        log.info("appId = {}; secret = {}; accessTokenUrl = {} ", appId, secret, accessTokenUrl);
        String accessTokenUrl = this.accessTokenUrl.concat("?corpid=").concat(appId).concat("&corpsecret=").concat(secret);
        log.info("accessTokenUrl --->>> {}", accessTokenUrl);
        ResponseEntity<String> exchange = restTemplate.exchange(accessTokenUrl, HttpMethod.GET, null, String.class);
        log.info("获取企业微信accessToken返回数据: {} ", JSONObject.toJSONString(exchange));
        AccessTokenResultDto accessTokenResultDto = JSONObject.parseObject(exchange.getBody(), AccessTokenResultDto.class);
        if (accessTokenResultDto == null || accessTokenResultDto.getErrcode() != 0 || Strings.isEmpty(accessTokenResultDto.getAccessToken())) {
            return error("对接微信获取accessToken失败! ".concat(accessTokenResultDto == null ? "接口返回null! " : accessTokenResultDto.getErrmsg()));
        }
        //缓存token
        redisService.setCacheObject(RedisKey.ACCESS_TOKEN_KEY, accessTokenResultDto.getAccessToken(), RedisKey.ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
        return success(accessTokenResultDto.getAccessToken());
    }


    public AjaxResult getQrCode(String sign) throws Exception {
        String url = URLEncoder.encode(redirectUrl, "UTF-8");
        String state = URLEncoder.encode(sign, "UTF-8");
        String getQrCodeUrl = qrCodeUrl.concat(appId).concat("&agentid=").concat(agentId).concat("&redirect_uri=").concat(url).concat("&state=").concat(state);
        log.info("生成企业微信登录二维码链接:  {}", getQrCodeUrl);
        return success(getQrCodeUrl);
    }


    public R<WxUserInfoDto> getUserInfoByCode(String code) {
        log.info("getUserInfoByCode--->>>{}", code);
        AjaxResult accessTokenResult = this.getAccessToken();
        if (!accessTokenResult.isSuccess()) {
            throw new ServiceException("获取accessToken出错! ");
        }
        String accessToken = accessTokenResult.get(MSG_TAG).toString();
        log.info("获取accessToken成功--->>> {} ", accessToken);
        String url = String.format(userInfoByCode, accessToken, code);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        log.info("调用微信根据Code获取用户信息接口返回--->>> {}", JSONObject.toJSONString(response));
        WxUserInfoResultDto wxUserInfoResultDto = JSONObject.parseObject(response.getBody(), WxUserInfoResultDto.class);
        if (wxUserInfoResultDto != null && wxUserInfoResultDto.getErrcode() == 0) {
            String userid = wxUserInfoResultDto.getUserid();
            String userTicket = wxUserInfoResultDto.getUserTicket();
            WxUserInfoDto wxUserInfoDto = new WxUserInfoDto();
            wxUserInfoDto.setUserid(userid);
            wxUserInfoDto.setUserTicket(userTicket);
            return R.ok(wxUserInfoDto);
        }else {
            String userid = "17620371344";
            WxUserInfoDto wxUserInfoDto = new WxUserInfoDto();
            wxUserInfoDto.setUserid(userid);
            log.info("调用失败--->>> getUserInfoByCode URL{},报错信息为{}", url,JSONObject.toJSONString(response));
            return R.ok(wxUserInfoDto);
        }
//        return R.fail(wxUserInfoResultDto == null ? "微信接口返回null! " : wxUserInfoResultDto.getErrmsg());
    }

    public SyncScrmInfoResultDto syncScrmUserInfo(SyncScrmUserInfoParamVo param) {
        List<ScrmUser> dataInfo = param.getDataInfo();
        List<UserInfoDto> userInfos = new ArrayList<>();
        dataInfo.forEach(d -> userInfos.add(JSONObject.parseObject(JSONObject.toJSONString(d), UserInfoDto.class)));
        userInfoRepository.saveOrUpdateByAccount(userInfos);
        return new SyncScrmInfoResultDto().success();
    }


    public SyncScrmInfoResultDto syncScrmRoleInfo(SyncScrmRoleInfoParamVo param) {
        List<ScrmRoleInfoDto> dataInfo = param.getDataInfo();
        List<RoleInfoDto> roleInfos = new ArrayList<>();
        dataInfo.forEach(d -> roleInfos.add(JSONObject.parseObject(JSONObject.toJSONString(d), RoleInfoDto.class)));
        roleInfoRepository.addBatchRoles(roleInfos);
        return new SyncScrmInfoResultDto().success();
    }


    public SyncScrmInfoResultDto syncScrmDeptInfo(SyncScrmDeptInfoParamVo param) {
        List<ScrmDeptInfoDto> dataInfo = param.getDataInfo();
        List<DeptInfoDto> deptInfos = new ArrayList<>();
        dataInfo.forEach(d -> deptInfos.add(JSONObject.parseObject(JSONObject.toJSONString(d), DeptInfoDto.class)));
        deptInfoRepository.addBatchDepts(deptInfos);
        return new SyncScrmInfoResultDto().success();
    }

    public R<AccountOnLineStatusOutPutDto> accountOnLineStatus(AccountOnLineStatusInputDto accountOnLineStatusInputDto) {
        log.info("进入method:accountOnLineStatus 入参为---->{}",JSON.toJSONString(accountOnLineStatusInputDto));
        String url = appUrl + accountOnLineStatus;
        // 加密字符串
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("employeeNo", accountOnLineStatusInputDto.getEmployeeNo());
        String encryptData = "";
        try {
            encryptData = CryptoUtils.publicKeyEncrypt(JSON.toJSONString(dataMap), basicDataPublicKey);
        } catch (Exception e) {
            log.info("加密数据报错");
        }
        jsonObject.put("data", encryptData);
        String json =  jsonObject.toJSONString();

        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("reqId", UuidUtils.generateUuid());
        headers.add("reqFrom", "PAD");
        headers.add("reqTime", DateUtil.getNowDateStr());
        headers.add("Authorization", "Basic c2NybXVzZXI6R2Fjc2NybUAxMjM=");
        //封装成一个请求对象
        HttpEntity request = new HttpEntity(json, headers);
        log.info("appUrl + accountOnLineStatus路径为{}，请求头为headers{}", url,headers);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(url, request, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        if(ObjectUtil.isEmpty(response)){
            throw  new  ServiceException("网络开小差，请联系管理员", CommCode.INTFR_OUTTER_INVOKE_ERROR.getCode());
        }

        AccountOnLineStatusOutPutDto accountOnLineStatusOutPutDto = null;
        try {
            accountOnLineStatusOutPutDto = JSONObject.parseObject(response.getBody(), AccountOnLineStatusOutPutDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ObjectUtil.isEmpty(accountOnLineStatusOutPutDto)){
            return R.fail(new AccountOnLineStatusOutPutDto());
        }

        log.info("外部接口正在返回body为{}",response.getBody());
        log.info("请求外部接口结束返回转换后出参{}", JSONObject.toJSONString(accountOnLineStatusOutPutDto));
//        if (null == accountOnLineStatusOutPutDto || "0".equals(accountOnLineStatusOutPutDto.getResultCode())){
//            AccountOnLineStatusOutPutDto accountOnLineStatusOutPutDto1 = new AccountOnLineStatusOutPutDto();
//            accountOnLineStatusOutPutDto1.setResultCode("1");
//            accountOnLineStatusOutPutDto1.setResultMessage("发补偿数据");
//            log.info("请求外部接口异常{}，补偿数据为{}", JSONObject.toJSONString(accountOnLineStatusOutPutDto), JSONObject.toJSONString(accountOnLineStatusOutPutDto1));
//            return R.ok(accountOnLineStatusOutPutDto1);
//        }
        log.info("进入method:accountOnLineStatus 执行结束返参为---->{}",JSON.toJSONString(accountOnLineStatusOutPutDto));
        return R.ok(accountOnLineStatusOutPutDto);
    }

    public R<ScrmPdiFileListOutputDto> getPdiFileList(ScrmPdiFileListInputDto scrmPdiFileListInputDto){
        String url = appUrl + getPdiFileList;
        // 加密字符串
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("vin", scrmPdiFileListInputDto.getVin());
        String encryptData = "";
        try {
            encryptData = CryptoUtils.publicKeyEncrypt(JSON.toJSONString(dataMap), basicDataPublicKey);
        } catch (Exception e) {
            log.info("加密数据报错");
        }
        jsonObject.put("data", encryptData);
        String json =  jsonObject.toJSONString();
        log.info("-jsonObject-->>> {}", JSONObject.toJSONString(jsonObject));
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("reqId", UuidUtils.generateUuid());
        headers.add("reqFrom", "PAD");
        headers.add("reqTime", DateUtil.getNowDateStr());
        headers.add("Authorization", "Basic c2NybXVzZXI6R2Fjc2NybUAxMjM=");
        //封装成一个请求对象
        HttpEntity request = new HttpEntity(json, headers);
        log.info("PDI请求头为{}",JSON.toJSONString(request));
        log.info("PDI请求路径为{}",url);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(url, request, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }

        if (ObjectUtil.isEmpty(response)){
            return R.fail(null,"查询PDI任务为null");
//            if (scrmPdiFileListOutputDto.getResultCode() > 0)
        }

        if (ObjectUtil.isEmpty(response.getBody())){
            return R.fail(null,"查询PDI任务为null");
//            if (scrmPdiFileListOutputDto.getResultCode() > 0)
        }
        log.info("PDI返回参数为{}",JSON.toJSONString(response.getBody()));
        ScrmPdiFileListOutputDto scrmPdiFileListOutputDto = JSONObject.parseObject(response.getBody(),ScrmPdiFileListOutputDto.class);


        log.info(JSONObject.toJSONString(scrmPdiFileListOutputDto));
        if (ObjectUtil.isNotEmpty(scrmPdiFileListOutputDto)){
            if(StringUtils.isNotEmpty(scrmPdiFileListOutputDto.getStatus())){
                String result = "1003".equals(scrmPdiFileListOutputDto.getStatus())? "1":"0";
                scrmPdiFileListOutputDto.setStatus(result);
            }
            List<ScrmPdiFileListDto> fileList = scrmPdiFileListOutputDto.getFileList();
            if (CollectionUtil.isNotEmpty(fileList)){
                for (ScrmPdiFileListDto scrmPdiFileListDto : fileList) {
                    if(StringUtils.isNotEmpty(scrmPdiFileListDto.getFileType())){
                        scrmPdiFileListDto.setFileType(ScrmToPadFileTypeEnum.getPadValueByScrmType(scrmPdiFileListDto.getFileType()));
                        log.info("PDI转换后的文件类型{}",JSON.toJSONString(scrmPdiFileListDto.getFileType()));
                    }
                }
            }
        }
        log.info("PDI执行结束");
        return R.ok(scrmPdiFileListOutputDto,scrmPdiFileListOutputDto.getResultMessage());
    }

    public R<ScrmPdiFileListOutputDto> getPdiSchema(ScrmPdiFileListInputDto scrmPdiFileListInputDto){
        String url = "https://malltest.gacmotor.com/thirdparty-app/api/wxapi/urlSchema";
        // 加密字符串
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("vin", scrmPdiFileListInputDto.getVin());
        String encryptData = "";
        try {
            encryptData = CryptoUtils.publicKeyEncrypt(JSON.toJSONString(dataMap), basicDataPublicKey);
        } catch (Exception e) {
            log.info("加密数据报错");
        }
        jsonObject.put("data", encryptData);
        String json =  jsonObject.toJSONString();

        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("reqId", UuidUtils.generateUuid());
        headers.add("reqFrom", "PAD");
        headers.add("reqTime", DateUtil.getNowDateStr());
        headers.add("Authorization", "Basic c2NybXVzZXI6R2Fjc2NybUAxMjM=");
        //封装成一个请求对象
        HttpEntity request = new HttpEntity(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        log.info(response.getBody());
        ScrmPdiFileListOutputDto scrmPdiFileListOutputDto = JSONObject.parseObject(response.getBody(),ScrmPdiFileListOutputDto.class);
        log.info(JSONObject.toJSONString(scrmPdiFileListOutputDto));
        return R.ok(scrmPdiFileListOutputDto);
    }

    public R<ScrmBasicDataOutputDto> getBasicData(ScrmBasicDataInputDto scrmBasicDataInputDto){
        String url = scrmUrl + getBasicData;
        // 加密字符串
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("type", scrmBasicDataInputDto.getType());

        String encryptData = "";
        try {
            encryptData = CryptoUtils.publicKeyEncrypt(JSON.toJSONString(dataMap), basicDataPublicKey);
        } catch (Exception e) {
            log.info("加密数据报错");
        }
        jsonObject.put("data", encryptData);
        jsonObject.put("systemCode","PAD");
        String json =  jsonObject.toJSONString();

        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //封装成一个请求对象
        HttpEntity request = new HttpEntity(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        log.info(response.getBody());
        ScrmBasicDataOutputDto scrmBasicDataOutputDto = JSONObject.parseObject(response.getBody(),ScrmBasicDataOutputDto.class);
        return R.ok(scrmBasicDataOutputDto);
    }

    public R<ScrmUserInfoToInstrumentOutputDto> getUserInfoByAccount(ScrmUserInfoInputDto scrmUserInfoInputDto){
        String url = scrmUrl + getUserInfoByAccount;
        // 加密字符串
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("account", scrmUserInfoInputDto.getAccount());

        String encryptData = "";
        try {
            encryptData = CryptoUtils.publicKeyEncrypt(JSON.toJSONString(dataMap), publicInterfaceKey);
        } catch (Exception e) {
            log.info("加密数据报错");
        }
        jsonObject.put("data", encryptData);
        String json =  jsonObject.toJSONString();

        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("reqId",UuidUtils.generateUuid());
        headers.set("reqFrom","PAD");
        headers.set("reqTime",DateUtil.getNowDateStr());
        headers.setContentType(MediaType.APPLICATION_JSON);

        //封装成一个请求对象
        HttpEntity request = new HttpEntity(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        log.info(response.getBody());
        ScrmUserInfoToInstrumentOutputDto scrmUserInfoToInstrumentOutputDto = JSONObject.parseObject(response.getBody(), ScrmUserInfoToInstrumentOutputDto.class);
        return R.ok(scrmUserInfoToInstrumentOutputDto);
    }

    public R<ScrmWxCropUserInfoOutputDto> getWxCropUserInfo(ScrmWxCropUserInfoInputDto scrmWxCropUserInfoInputDto){
        log.info("进入method:getWxCropUserInfo--->>> {}", JSON.toJSONString(scrmWxCropUserInfoInputDto));
        String url = scrmUrl + getWxCropUserInfo;
        log.info("外部接口调用地址--->>> {}", url);
        // 加密字符串
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userId", scrmWxCropUserInfoInputDto.getUserId());
        log.info("外部接口调用地址--->>> {}", JSONObject.toJSONString(dataMap));
        String encryptData = "";
        try {
            encryptData = CryptoUtils.publicKeyEncrypt(JSON.toJSONString(dataMap), publicInterfaceKey);
        } catch (Exception e) {
            log.info("加密数据报错");
        }
        jsonObject.put("data", encryptData);
        String json =  jsonObject.toJSONString();

        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("reqId",UuidUtils.generateUuid());
        headers.set("reqFrom","PAD");
        headers.set("reqTime",DateUtil.getNowDateStr());
        headers.setContentType(MediaType.APPLICATION_JSON);
        //封装成一个请求对象
        HttpEntity request = new HttpEntity(json, headers);
        log.info("外部接口调用地址--->>> request{}", JSONObject.toJSONString(request));
        ScrmWxCropUserInfoOutputDto scrmWxCropUserInfoOutputDto = new ScrmWxCropUserInfoOutputDto();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(url, request, String.class);
            log.info("response = restTemplate.postForEntity 返回信息》》》{}",JSONObject.toJSONString(response));
        } catch (RestClientException e) {
            log.info("restTemplate.postForEntity调用接口异常--->>> request{}",JSONObject.toJSONString(response));
            e.printStackTrace();
        }
        if (ObjectUtil.isEmpty(response)){
            return R.fail(scrmWxCropUserInfoOutputDto,"账号信息查无数据");
        }
        log.info("外部接口调用返回body--->>> request{}",response.getBody());

        try {
             scrmWxCropUserInfoOutputDto = JSONObject.parseObject(response.getBody(), ScrmWxCropUserInfoOutputDto.class);
        } catch (Exception e) {
            log.info("JSONObject.parseObject转换异常--->>> scrmWxCropUserInfoOutputDto 》》》{}",JSONObject.toJSONString(response.getBody()));
            e.printStackTrace();
        }

        log.info("method:getWxCropUserInfo() 执行结束");
        return R.ok(scrmWxCropUserInfoOutputDto);
    }

    public R getProductQRcode(ScrmEncrypeParamVo paramVo) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("account", paramVo.getData());
        String encryptData = "";
        try {
            encryptData = CryptoUtils.publicKeyEncrypt(JSON.toJSONString(dataMap), publicInterfaceKey);
        } catch (Exception e) {
            log.info("加密数据报错");
        }
        log.info("encryptData 加密后{}",encryptData);
        jsonObject.put("data", encryptData);
        String json =  jsonObject.toJSONString();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.add("reqId", UuidUtils.generateUuid());
        headers.add("reqFrom", "PAD");
        headers.add("reqTime", DateUtil.getNowDateStr());
        headers.add("Authorization", qcodeAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity request = new HttpEntity(json, headers);
        log.info("打印调用SCRM入参request{}",JSON.toJSONString(request));
//        String getProductQRcode = "https://test-gacscrmapp.gacmotor.com/gac-data-sync/interface/external/common/getConsultantDynamicCode";
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(getProductQRcode, request, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            log.info("捕获异常打印getProductQRcode{}",JSON.toJSONString(response));
        }
        if (ObjectUtil.isEmpty(response)){
            return R.fail(null,"账号信息异常，请联系账号管理员");
        }
        ScrmConsultantCodeOutBO scrmConsultantCodeOutBO = JSONObject.parseObject(response.getBody(), ScrmConsultantCodeOutBO.class);
        log.info(" scrmConsultantCodeOutBO1{}",JSON.toJSONString(scrmConsultantCodeOutBO));
        if (ObjectUtil.isEmpty(scrmConsultantCodeOutBO)){
           return R.fail(null,"账号数据异常，请联系账号管理员");
        }
        if (!"200".equals(scrmConsultantCodeOutBO.getCode())){
            return R.fail(null,"非法信息,请检查信息");
        }
        ScrmConsultantCodeVo data = scrmConsultantCodeOutBO.getData();
        log.info("打印成功信息{}",JSON.toJSONString(data));
        return R.ok(data.getQrcodeUrl(),"200".equals(scrmConsultantCodeOutBO.getCode())?"活码调用成功":scrmConsultantCodeOutBO.getMessage());
    }

    public R<ScrmConsultantCodeOutBO> getConsultantDynamicCode(ScrmConsultantCodeInputBO scrmConsultantCodeInputBO) {

        return null;
    }



}

