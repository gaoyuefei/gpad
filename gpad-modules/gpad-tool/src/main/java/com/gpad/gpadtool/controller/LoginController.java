package com.gpad.gpadtool.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.web.domain.AjaxResult;
import com.gpad.gpadtool.constant.RedisKey;
import com.gpad.gpadtool.domain.vo.ScanCodeTokenInfoVo;
import com.gpad.gpadtool.service.ScrmService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName LoginController.java
 * @Description 登录接口
 * @createTime 2023年09月25日 10:13:00
 */
@Api(value = "登录接口", tags = "登录接口")
@RestController
@RequestMapping("/api")
public class LoginController {

    protected final Logger log = LogManager.getLogger(this.getClass());

    @Value("${sign.getTokenRollBACK}")
    private String getTokenRollBACK;

    @Autowired
    private ScrmService scrmService;

    @Autowired
    private RedisTemplate redisTemplate;

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
}
