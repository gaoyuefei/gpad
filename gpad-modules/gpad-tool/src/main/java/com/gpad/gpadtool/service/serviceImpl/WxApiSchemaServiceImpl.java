package com.gpad.gpadtool.service.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.gpadtool.domain.dto.wxapi.ExhibitionMixPadInputBO;
import com.gpad.gpadtool.domain.dto.wxapi.WxApiCommentInputBO;
import com.gpad.gpadtool.domain.dto.wxapi.outBo.ExhibitionMixPadOutBO;
import com.gpad.gpadtool.domain.dto.wxapi.outBo.WxApiCommentOutBO;
import com.gpad.gpadtool.domain.vo.LoginResVo;
import com.gpad.gpadtool.domain.vo.OrderCommentUrlVo;
import com.gpad.gpadtool.domain.vo.WxApiGetCommentVo;
import com.gpad.gpadtool.domain.vo.WxTokenVO;
import com.gpad.gpadtool.enums.WxToPadSchemaUrlTypeEnum;
import com.gpad.gpadtool.service.WxApiSchemaService;
import com.gpad.gpadtool.utils.UrlSchemaUntils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;


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

    @Value("${wx.sitApp-schemaUrl}")
    private String sitAppSchemaUrl;

    @Value("${wx.app-commentUrlExt}")
    private String commentUrlExt;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public R<String> getgetSkipSchemaUrl(String wxApiSchemaUrl) {
        String str = "weixin://dl/business/?t=F06tNGRdOcj";
        LoginResVo tokenUerName = UrlSchemaUntils.getTokenUerName();
        log.info("data参数获取成功1:  {}", JSON.toJSONString(tokenUerName));

        WxTokenVO wxTokenVO = getAppToken(tokenUerName);
        log.info("获取加密后得token2为:  {}", JSON.toJSONString(wxTokenVO));

        String skipSchemaUrl = getSkipSchemaUrl(wxTokenVO.getToken(),wxApiSchemaUrl);
        log.info("skipSchemaUrl加密后的数据3:  {}", skipSchemaUrl);
        log.info("method:getgetSkipSchemaUrl 执行结束");
        return R.ok(skipSchemaUrl,"获取成功");
    }

    @Override
    public R sitUrlSchema(String wxApiSchemaUrl) {
        String str = sitAppSchemaUrl;
        log.info("跳转码获取为:  {}", str);
        return  R.ok(str);
    }

    @Override
    public R getOrderComment(WxApiCommentInputBO wxApiCommentInputBO) {

        LoginResVo tokenUerName = UrlSchemaUntils.getTokenUerName();
        log.info("data参数获取成功1:  {}", JSON.toJSONString(tokenUerName));

        WxTokenVO wxTokenVO = getAppToken(tokenUerName);
        log.info("获取加密后得token2为:  {}", JSON.toJSONString(wxTokenVO));

        WxApiCommentOutBO wxApiCommentOutBO = getOrderCommentStatus(wxTokenVO,wxApiCommentInputBO);
        if (ObjectUtil.isEmpty(wxApiCommentOutBO)){
            return R.ok(null,"查无数据");
        }
        if (!"0000".equals(wxApiCommentOutBO.getCode())){
            return R.fail(wxApiCommentOutBO.getSuccess(),wxApiCommentOutBO.getMsg());
        }
        return R.ok(wxApiCommentOutBO.getData(),wxApiCommentOutBO.getMsg());
    }

    @Override
    public R queryExhibitionMixPad(ExhibitionMixPadInputBO exhibitionMixPadInputBO) {

        LoginResVo tokenUerName = UrlSchemaUntils.getTokenUerName();
        log.info("data参数获取成功1:  {}", JSON.toJSONString(tokenUerName));

        WxTokenVO wxTokenVO = getAppToken(tokenUerName);
        log.info("获取加密后得token2为:  {}", JSON.toJSONString(wxTokenVO));
        ExhibitionMixPadOutBO exhibitionMixPadOutBO = queryExhibitionMixPadExt(exhibitionMixPadInputBO, wxTokenVO);
        return R.ok(exhibitionMixPadOutBO.getData(),exhibitionMixPadOutBO.getMsg());
    }

    @Override
    public R getOrderCommentUrl(OrderCommentUrlVo orderCommentUrlVo) {
        if (StringUtils.isNotEmpty(orderCommentUrlVo.getOrderNo()) && StringUtils.isNotEmpty(orderCommentUrlVo.getId())){
            return R.fail(500,"参数异常，请检查参数");
        }

        //兼容前端跳转路径接口  2023/11/09
        LoginResVo tokenUerName = UrlSchemaUntils.getTokenUerName();
        log.info("data参数获取成功1:  {}", JSON.toJSONString(tokenUerName));

        WxTokenVO wxTokenVO = getAppToken(tokenUerName);
        log.info("获取加密后得token2为:  {}", JSON.toJSONString(wxTokenVO));

        String orderCommentUrl = "type="+ orderCommentUrlVo.getType() +"&";
        if (StringUtils.isNotEmpty(orderCommentUrlVo.getOrderNo())){
            String orderNo = orderCommentUrlVo.getOrderNo();
            if (orderNo.contains("-")){
                orderNo = orderNo.substring(0, orderNo.indexOf("-"));
                log.info("长订单截取后：{}",orderNo);
            }
            log.info("拼接结束后：{}",orderNo);
            orderCommentUrl  =  orderCommentUrl+ "orderNo="+ orderNo + "&" + "channel=" +orderCommentUrlVo.getChannel();
        }

        if (StringUtils.isNotEmpty(orderCommentUrlVo.getId())){
            orderCommentUrl  =  orderCommentUrl+ "id=" + orderCommentUrlVo.getId() +"&"+ "channel=" + orderCommentUrlVo.getChannel();
        }
        log.info("拼接完参数：{}",orderCommentUrl);
       String skipSchemaUrl = getOrderCommentUrlExt(wxTokenVO.getToken(),orderCommentUrl);
        log.info("skipSchemaUrl加密后的数据3:  {}", skipSchemaUrl);
        log.info("method:getgetSkipSchemaUrl 执行结束");
        return R.ok(skipSchemaUrl,"获取成功");
    }

    public String getOrderCommentUrlExt(String token, String orderCommentUrl) {
        //  orderCommentUrl = "detail/post-comment?type=0&id=741093587770&channel=small_channel";

        log.info("进入到连接获取接口:  {},入参为{}", JSONObject.toJSONString(token),orderCommentUrl);
        String data = "";

        String url1 = appSchemaUrl;
        String str = "src=";
        String url = commentUrlExt; //https://malltest.gacmotor.com/
        String str1 = "detail/post-comment?";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appId", "wx86a1eb5a53a6973b");
        jsonObject.put("envVersion", "release");
        jsonObject.put("path", "pages/web");
        String activiveURL =  commentUrlExt + str1 + orderCommentUrl;
        String encodeUrl = null;
        try {
            encodeUrl = URLEncoder.encode(activiveURL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        jsonObject.put("query", str + encodeUrl);
        String json = com.alibaba.fastjson.JSONObject.toJSONString(jsonObject);
        log.info("加密后的数据:  {}", json);
//        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        //封装成一个请求对象
        HttpEntity request = new HttpEntity(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(appSchemaUrl, request, String.class);
        log.info("请求对象加密后的数据:  {}", JSONObject.toJSONString(response.getBody()));
        log.info("到连接获取接口:  {},入参为{}", JSONObject.toJSONString(token),orderCommentUrl);
        if (ObjectUtil.isNotEmpty(response)){
            String body = response.getBody();
            if(StringUtils.isNotEmpty(body)){
                data = Objects.requireNonNull(JSONObject.parseObject(body)).getString("data");
            }
        }
        return data;

    }

    public ExhibitionMixPadOutBO queryExhibitionMixPadExt(ExhibitionMixPadInputBO exhibitionMixPadInputBO, WxTokenVO wxTokenVO) {
        // commentUrlExt
        String url =  "https://malltest.gacmotor.com/big-screen-bff/fronted/exhibition/content/queryExhibitionMisePad";

        String token = wxTokenVO.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deriveCode", exhibitionMixPadInputBO.getPackageCode());
        String json = com.alibaba.fastjson.JSONObject.toJSONString(jsonObject);
        //封装成一个请求对象
        HttpEntity request = new HttpEntity(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        ExhibitionMixPadOutBO exhibitionMixPadOutBO = com.alibaba.fastjson.JSONObject.parseObject(response.getBody(), ExhibitionMixPadOutBO.class);
        return exhibitionMixPadOutBO;
    }

    public WxApiCommentOutBO getOrderCommentStatus(WxTokenVO wxTokenVO,WxApiCommentInputBO wxApiCommentInputBO) {
        String url = "https://malltest.gacmotor.com" +"/thirdparty-app/scrm/getOrderComment";

        String token = wxTokenVO.getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        String orderNo = wxApiCommentInputBO.getOrderNo();
        if (orderNo.contains("-")){
            orderNo = orderNo.substring(0, orderNo.indexOf("-"));
            log.info("长订单截取后：{}",orderNo);
        }
        log.info("截取后订单号为：{}",orderNo);
        jsonObject.put("orderNo", orderNo);
        String json = com.alibaba.fastjson.JSONObject.toJSONString(jsonObject);
        //封装成一个请求对象
        HttpEntity request = new HttpEntity(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        log.info("method：getOrderCommentStatus 返回JSON数据为:  {}", JSON.toJSONString(response));
        WxApiCommentOutBO wxApiCommentOutBO = com.alibaba.fastjson.JSONObject.parseObject(response.getBody(), WxApiCommentOutBO.class);
        if (ObjectUtil.isEmpty(wxApiCommentOutBO)){
            return null;
        }
        if (!"0000".equals(wxApiCommentOutBO.getCode())){
            return wxApiCommentOutBO;
        }
        if (!wxApiCommentOutBO.getSuccess()){
            return wxApiCommentOutBO;
        }
        WxApiGetCommentVo data = wxApiCommentOutBO.getData();
        data.setOrderType(WxToPadSchemaUrlTypeEnum.getPadValueByWxType(data.getOrderType()));
        wxApiCommentOutBO.setData(data);
        log.info("打印转换后得返回orderType：{}",orderNo);
        return wxApiCommentOutBO;
    }


    public String getSkipSchemaUrl(String token,String wxApiSchemaUrl) {
        log.info("进入到连接获取接口:  {},入参为{}", JSONObject.toJSONString(token),wxApiSchemaUrl);
        String data = "";
        String url = appSchemaUrl;
        String str = "src=";
        String wxApiUrl = "https://pad-test.spgacmotorfm.com/";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appId", "wx86a1eb5a53a6973b");
        jsonObject.put("envVersion", "release");
        jsonObject.put("path", "pages/web");

        String encodeUrl = null;
        try {
            encodeUrl = URLEncoder.encode(wxApiUrl + wxApiSchemaUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        jsonObject.put("query", str + encodeUrl);
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
        log.info("到连接获取接口:  {},入参为{}", JSONObject.toJSONString(token),wxApiSchemaUrl);
        if (ObjectUtil.isNotEmpty(response)){
            String body = response.getBody();
            if(StringUtils.isNotEmpty(body)){
                 data = Objects.requireNonNull(JSONObject.parseObject(body)).getString("data");
            }
        }
        return data;
    }

//    public static void main(String[] args) {
//        String str = "https://pad-test.spgacmotorfm.com/pages/deliveryCeremony/index";
//        String str = ?bussinessNo=DSODGDA0102021050700001
//        String encodeUrl = null;
//        try {
//            encodeUrl = URLEncoder.encode(str, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(encodeUrl);
//    }

    public WxTokenVO getAppToken(LoginResVo tokenUerName) {
        log.info("进入获取小程序token接口->>>>>{}",JSONObject.toJSONString(tokenUerName));
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
        log.info("执行结束->>>>>{}",JSONObject.toJSONString(wxTokenVO));
        //TODO 判断
        return wxTokenVO;
    }


}

