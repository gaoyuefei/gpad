package com.gpad.gpadtool.controller;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.common.core.web.domain.AjaxResult;
import com.gpad.common.security.service.TokenService;
import com.gpad.common.security.utils.SecurityUtils;
import com.gpad.gpadtool.constant.RedisKey;
import com.gpad.gpadtool.domain.vo.JssdkTicketVO;
import com.gpad.gpadtool.domain.vo.JssdkVo;
import com.gpad.gpadtool.domain.vo.ScanCodeTokenInfoVo;
import com.gpad.gpadtool.service.ScrmService;
import com.gpad.gpadtool.utils.Sha1Util;
import com.gpad.system.api.domain.SysUser;
import com.gpad.system.api.model.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;

import static com.gpad.common.core.web.domain.AjaxResult.MSG_TAG;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName LoginController.java
 * @Description 登录接口
 * @createTime 2023年09月25日 10:13:00
 */
@Api(value = "登录接口", tags = "登录接口")
@RefreshScope
@RestController
@RequestMapping("/api")
public class LoginController {

    protected final Logger log = LogManager.getLogger(this.getClass());

    @Value("${sign.getTokenRollBACK}")
    private String getTokenRollBACK;

    @Value("${wx.appId}")
    private String wx_appId;

    @Autowired
    private ScrmService scrmService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TokenService tokenService;

    /**
     * 获取当前账号 account
     */
    @Operation(summary = "生成二维码")
    @GetMapping("/getCurrentTokenAcount")
    public R getQrCode(HttpServletRequest request) {
        String username = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(username)){
            R.fail(1000009,"当前令牌信息异常");
        }
        return R.ok(username);
    }

    /**
     * 生成二维码
     */
    @Operation(summary = "生成二维码")
    @GetMapping("/getQrCode")
    public AjaxResult getQrCode(@RequestParam("sign") String sign) throws Exception {
        return scrmService.getQrCode(sign);
    }

    /**
     * 获取token
     */
    @Operation(summary = "获取token")
    @GetMapping("/getToken")
    public R<ScanCodeTokenInfoVo> getToken(@RequestParam("sign") String sign){
//        ScanCodeTokenInfoVo scanCodeTokenInfoVo = new ScanCodeTokenInfoVo();
//        scanCodeTokenInfoVo.setCode("200");
//        scanCodeTokenInfoVo.setExpressTime("180");
//        scanCodeTokenInfoVo.setMsg("nihao");
//        scanCodeTokenInfoVo.setToken("123");
        try {
            Object o = redisTemplate.opsForValue().get(sign);
            String string = JSONObject.toJSONString(o);
            System.out.println(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("获取token! sign {}",sign);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(sign.toUpperCase(Locale.ROOT)))){
            //通过sign查redis缓存的token
            String token = redisTemplate.opsForValue().get(sign.toUpperCase(Locale.ROOT))+"";
            if (Strings.isNotEmpty(token)){
                log.info("获取token! token1 {}",token);
                //能查到，返回token
                return R.ok(JSONObject.parseObject(token, ScanCodeTokenInfoVo.class));
            }
            String token2 = redisTemplate.opsForValue().get(sign)+"";
            if (Strings.isNotEmpty(token)){
                log.info("获取token! token2 {}",token);
                //能查到，返回token
                return R.ok(JSONObject.parseObject(token2, ScanCodeTokenInfoVo.class));
            }
        }else {
            String token3 = redisTemplate.opsForValue().get(sign) +"";
            return R.ok(JSONObject.parseObject(token3, ScanCodeTokenInfoVo.class));
        }
        //查不到返回失败
        return R.fail();
    }


    /**
     * 手动补偿回调token
     */
    @Operation(summary = "手动补偿回调token")
    @GetMapping("/getTokenRollBACK")
    public R<ScanCodeTokenInfoVo> getTokenRollBACK(@RequestParam("sign") String sign){
        ScanCodeTokenInfoVo scanCodeTokenInfoVo = new ScanCodeTokenInfoVo();
        scanCodeTokenInfoVo.setCode("200");
        scanCodeTokenInfoVo.setExpressTime("180");
        scanCodeTokenInfoVo.setMsg("失败手动补偿");
        scanCodeTokenInfoVo.setToken("getTokenRollBACK");
        redisTemplate.opsForValue().set("sign",JSON.toJSONString(scanCodeTokenInfoVo));
        //查不到返回失败
        return R.ok(scanCodeTokenInfoVo);
    }



    /**
     * 方便调试接口
     */
    @Operation(summary = "手动登录账号")
    @GetMapping("/userName/passWord")
    public R getTokenRollBACK(@RequestParam("userName") String userName,@RequestParam("passWord") String passWord, @RequestParam(required = false, value = "dealerCode") String dealerCode){
        if (!"boyue2024".equals(passWord)){
            return R.fail();
        }
        LoginUser loginUser = new LoginUser();

        SysUser sysUser = new SysUser();
        sysUser.setUserId(System.currentTimeMillis());
        sysUser.setUserName(userName);

        loginUser.setDealerCode(dealerCode);
        loginUser.setSysUser(sysUser);
        loginUser.setUsername(userName);

        Map<String, Object> token = tokenService.createToken(loginUser);
        return R.ok(token);
    }


    /**
     * app端退出登录
     */
    @Operation(summary = "退出登录")
    @GetMapping("/logout")
    public void logout(HttpServletRequest request) {
        //删除redis中的tokenId
        String token = request.getHeader(RedisKey.PAD_TOKEN_KEY);
        Boolean deleteToken = redisTemplate.delete(token);
        log.info("移除token! {}",deleteToken);
    }
    @Autowired
    private RestTemplate restTemplate;
    /**
     *
     */
    @Operation(summary = "JS-SDK使用权限签名")
    @GetMapping("/js_sdk/getUrl")
    public R getJsdk(@RequestParam("url") String url) {
        log.info("SDK使用权限签名url! {}",url);
        String url1 = "https://pad-test.spgacmotorfm.com/pages/orderList/deliveryManagementList?account=Jy+6RjqtL/h8oOJeqM1h72RKaQISTO0Umii3BxergFxK9KMrItp5TXjdXbvemZYdi4ZQ2pxjPe328p1MgpKJ2UGEUTe8MUlfILgopMkq2/evxzEUx0Qd2BIXlXkOYQHc7XjqqxlgEvMLzO+MM6EzsxANffRZE2sKoJ0xlA5a5IY=";
        long ts = System.currentTimeMillis() / 1000;
        JssdkVo jssdkVo = new JssdkVo();
        jssdkVo.setAppId(wx_appId);
        jssdkVo.setTimeTamp(ts+"");
        try {
            jssdkVo.setNonceStr(Sha1Util.sha1(ts+""));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String ticket = getJsApiTicket();
        String signature  = "jsapi_ticket="+ticket+"&"+ "noncestr="+jssdkVo.getNonceStr()+"&" +"timestamp="+jssdkVo.getTimeTamp()+"&"+"url="+url1;
        log.info("signature {}",JSONObject.toJSONString(signature));
        try {
            jssdkVo.setSignature(Sha1Util.sha1(signature));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        log.info("jssdkVo {}",JSONObject.toJSONString(jssdkVo));
        return R.ok(jssdkVo);
    }

    private String getJsApiTicket() {
        //获取token
        AjaxResult accessToken = scrmService.getAccessToken();
        String token = accessToken.get(MSG_TAG) +"";
        log.info("token {}",JSONObject.toJSONString(token));
        //获取验证签名
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>( httpHeaders);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket";
        url = url.concat("?access_token=").concat(token);
        url= url.concat("&debug=1");
        log.info("调用url {}",JSONObject.toJSONString(url));
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        if (ObjectUtil.isEmpty(response)){
            throw new ServiceException("网络开小差，请稍后重试",500);
        }
        JssdkTicketVO jssdkTicketVO = null;
        try {
            jssdkTicketVO = JSONObject.parseObject(response.getBody(), JssdkTicketVO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("返回签证是 {}",JSONObject.toJSONString(jssdkTicketVO));
        if (ObjectUtil.isEmpty(jssdkTicketVO)){
            throw new ServiceException("生成签名失败，请检查参数",500);
        }
        return jssdkTicketVO.getTicket();
    }

}
