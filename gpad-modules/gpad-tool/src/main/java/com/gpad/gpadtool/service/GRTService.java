package com.gpad.gpadtool.service;

import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.*;
import com.gpad.gpadtool.utils.DateUtil;
import com.gpad.gpadtool.utils.UuidUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName GRTService.java
 * @Description TODO
 * @createTime 2023年09月22日 11:01:00
 */
@Service
public class GRTService {
    @Value("${grt.url.orderList}")
    private String orderListUrl;
    @Value("${grt.url.orderDetail}")
    private String orderDetailUrl;
    @Value("${grt.url.changeOrderStatus2Grt}")
    private String changeOrderStatus2GrtUrl;
    @Value("${grt.url.orderReserveInfo}")
    private String orderReserveInfoUrl;


    @Autowired
    private RestTemplate restTemplate;


    public R<List<OrderNoResultDto>> getGrtOrders(OrderNoListParamVo orderNoListParamVo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        String reqId = UuidUtil.generateUuid();
        String reqFrom = "PAD";
        String reqTime =  DateUtil.getNowDateStr();
        httpHeaders.add("reqId", reqId);
        httpHeaders.add("reqFrom",reqFrom);
        httpHeaders.add("reqTime", reqTime);
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        String url = "";
        if (Strings.isNotEmpty(orderNoListParamVo.getUserCode())){
            url = orderListUrl.contains("?") ?
                    orderListUrl.concat("&userCode=").concat(orderNoListParamVo.getUserCode())
                    : orderListUrl.concat("?userCode=").concat(orderNoListParamVo.getUserCode());
        }
        if (Strings.isNotEmpty(orderNoListParamVo.getDealerCode())){
            url = orderListUrl.contains("?") ?
                    orderListUrl.concat("&dealerCode=").concat(orderNoListParamVo.getDealerCode())
                    : orderListUrl.concat("?dealerCode=").concat(orderNoListParamVo.getDealerCode());
        }
        if (Strings.isNotEmpty(orderNoListParamVo.getUpdatedStart())){
            url = orderListUrl.contains("?") ?
                    orderListUrl.concat("&updatedStart=").concat(orderNoListParamVo.getUpdatedStart())
                    : orderListUrl.concat("?updatedStart=").concat(orderNoListParamVo.getUpdatedStart());
        }
        if (Strings.isNotEmpty(orderNoListParamVo.getUpdatedEnd())){
            url = orderListUrl.contains("?") ?
                    orderListUrl.concat("&updatedEnd=").concat(orderNoListParamVo.getUpdatedEnd())
                    : orderListUrl.concat("?updatedEnd=").concat(orderNoListParamVo.getUpdatedEnd());
        }

        ResponseEntity<OrderNoListResultDto> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, OrderNoListResultDto.class);

//        try {
//            grtRequestLogService.save(new ApiRequestLog(reqId,reqFrom,reqTime,JSONObject.toJSONString(orderNoListParamVo), JSONObject.toJSONString(response),
//                    response.getBody()==null?null:response.getBody().getStatus()));
//        }catch (Exception e){
//            log.error("保存API调用日志出错! {}",e.getMessage());
//        }

        OrderNoListResultDto orderNoListResultDto = response.getBody();
        if (orderNoListResultDto == null || orderNoListResultDto.getStatus() == null){
            return R.fail("对接GRT获取待交车订单列表出错!  接口返回null! " );
        }else if (!orderNoListResultDto.getStatus().equals("200")){
            return R.fail("对接GRT获取待交车订单列表出错!  ".concat(orderNoListResultDto.getMessage() == null ? "接口返回null! " : orderNoListResultDto.getMessage()));
        }
        return R.ok(orderNoListResultDto.getData());
    }

    public R<List<OrderDetailResultDto>> getOrderDetailByNo(String businessNo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("reqId", UuidUtil.generateUuid());
        httpHeaders.add("reqFrom","PAD");
        httpHeaders.add("reqTime", DateUtil.getNowDateStr());
        HttpEntity<String> requestEntity = new HttpEntity<>( httpHeaders);

        String url = String.format(orderDetailUrl,businessNo);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        OrderDetailListResultDto orderDetailListResultDto = JSONObject.parseObject(response.getBody(), OrderDetailListResultDto.class);

        if (orderDetailListResultDto == null || orderDetailListResultDto.getStatus() == null){
            return R.fail("对接GRT获取待交车订单详情出错!接口返回null  ");
        }else if (!orderDetailListResultDto.getStatus().equals("200")){
            return R.fail("对接GRT获取待交车订单详情出错!  ".concat(orderDetailListResultDto.getMessage()));
        }
        return R.ok(orderDetailListResultDto.getData());
    }

    public R<Void> changeOrderStatus2Grt(OrderStatusVo orderStatusVo){

        ResponseEntity<BaseGrtResultDto> response = restTemplate.exchange(changeOrderStatus2GrtUrl, HttpMethod.POST, new HttpEntity<>(orderStatusVo), BaseGrtResultDto.class);
        if (response.getStatusCode() != HttpStatus.OK){
            return R.fail(response.getBody() == null?"null" : response.getBody().getMessage());
        }
        if (response.getBody()==null || response.getBody().getStatus().equals("500")){
            return R.fail("调用GRT推送订单状态变更失败!  错误信息: "+(response.getBody()==null?"null":response.getBody().getMessage()));
        }
        return R.ok(null,response.getBody().getMessage());
    }

    public R<Void> orderReserveInfo(OrderReserveVo orderReserveVo){

        ResponseEntity<BaseGrtResultDto> response = restTemplate.exchange(orderReserveInfoUrl, HttpMethod.POST, new HttpEntity<>(orderReserveVo), BaseGrtResultDto.class);
        if (response.getStatusCode() != HttpStatus.OK){
            return R.fail(response.getBody() == null?"null" : response.getBody().getMessage());
        }
        if (response.getBody()==null || response.getBody().getStatus().equals("500")){
            return R.fail("调用GRT推送预约交车数据失败!  错误信息: "+(response.getBody()==null?"null":response.getBody().getMessage()));
        }
        return R.ok(null,response.getBody().getMessage());
    }
}
