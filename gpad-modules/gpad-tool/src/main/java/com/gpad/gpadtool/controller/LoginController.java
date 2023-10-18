package com.gpad.gpadtool.controller;

import com.gpad.common.core.domain.R;
import com.gpad.common.core.web.domain.AjaxResult;
import com.gpad.gpadtool.constant.RedisKey;
import com.gpad.gpadtool.service.ScrmService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
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
    public R<String> getToken(@RequestParam("sign") String sign){
        log.info("获取token! sign {}",sign);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(sign.toUpperCase(Locale.ROOT)))){
            //通过sign查redis缓存的token
            String token = redisTemplate.opsForValue().get(sign.toUpperCase(Locale.ROOT)).toString();
            if (Strings.isNotEmpty(token)){
                log.info("获取token! token {}",token);
                //能查到，返回token
                return R.ok(token);
            }
        }

        //查不到返回失败
        return R.fail();
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
