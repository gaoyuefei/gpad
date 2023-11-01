package com.gpad.gpadtool.service.serviceImpl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.vo.LoginResVo;
import com.gpad.gpadtool.domain.vo.ResponseEntityVo;
import com.gpad.gpadtool.domain.vo.WxTokenVO;
import com.gpad.gpadtool.service.WxApiSchemaService;
import com.gpad.gpadtool.utils.UrlSchemaUntils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.service.serviceImpl
 * @Author: LF
 * @CreateTime: 2023-10-30
 * @Description: 获取微信跳转连接
 * @Version: 1.0
 */
@Slf4j
@Service
public class WxApiSchemaServiceImpl implements WxApiSchemaService {


    @Value("${wx.app-schemaUrl}")
    private String appSchemaUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public R<String> getgetSkipSchemaUrl(String wxApiSchemaUrl) {
        String str = "weixin://dl/business/?t=F06tNGRdOcj";
        LoginResVo tokenUerName = UrlSchemaUntils.getTokenUerName();
        WxTokenVO wxTokenVO = getAppToken(tokenUerName);
        String skipSchemaUrl = getSkipSchemaUrl(wxTokenVO.getToken(),wxApiSchemaUrl);
        log.info("skipSchemaUrl加密后的数据:  {}", skipSchemaUrl);
        return R.ok(skipSchemaUrl,"获取成功");
    }


    public String getSkipSchemaUrl(String token,String wxApiSchemaUrl) {
        String url = appSchemaUrl;
        String wxApiUrl = "src=https://pad-test.spgacmotorfm.com/h5/";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appId", "wx86a1eb5a53a6973b");
        jsonObject.put("envVersion", "release");
        jsonObject.put("path", "pages/web");
        jsonObject.put("query", wxApiUrl + wxApiSchemaUrl);
        String json = com.alibaba.fastjson.JSONObject.toJSONString(jsonObject);
        log.info("加密后的数据:  {}", json);
//        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        //封装成一个请求对象
        HttpEntity request = new HttpEntity(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        log.info("请求对象加密后的数据:  {}", JSONObject.toJSONString(response.getBody()));
        return response.getBody();
    }

    public WxTokenVO getAppToken(LoginResVo tokenUerName) {
        String url = "https://malltest.gacmotor.com/boss-admin-app/getToken";
        HttpEntity<LoginResVo> loginResVoHttpEntity = new HttpEntity<>(tokenUerName);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, loginResVoHttpEntity, String.class);
        String data = JSONObject.parseObject(exchange.getBody()).getString("data");
        String token = JSONObject.parseObject(data).getString("token");
        String expirationTime = JSONObject.parseObject(data).getString("expirationTime");
        WxTokenVO wxTokenVO = new WxTokenVO();
        wxTokenVO.setToken(token);
        wxTokenVO.setExpirationTime(expirationTime);
        log.info("token参数获取为->>>>>{}",JSONObject.toJSONString(wxTokenVO));
        log.info("token参数获取为->>>>>{}",JSONObject.toJSONString(exchange));
        //TODO 判断
        return wxTokenVO;
    }


}

