package com.gpad.gpadtool.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.gpadtool.constant.FlowNodeNum;
import com.gpad.gpadtool.domain.dto.*;
import com.gpad.gpadtool.domain.entity.OrderDetail;
import com.gpad.gpadtool.enums.GrtToPadEnums;
import com.gpad.gpadtool.repository.FlowInfoRepository;
import com.gpad.gpadtool.repository.OrderDetailRepository;
import com.gpad.gpadtool.utils.DateUtil;
import com.gpad.gpadtool.utils.GRTSignUtil;
import com.gpad.gpadtool.utils.RedisLockUtils;
import com.gpad.gpadtool.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName GRTService.java
 * @Description TODO
 * @createTime 2023年09月22日 11:01:00
 */
@Slf4j
@Service
public class GRTService {
    @Value("${grt.url.orderList}")
    private String orderListUrl;
    @Value("${grt.url.orderDetail}")
    private String orderDetailUrl;
    @Value("${grt.url.changeOrderStatus2Grt}")
    private String changeOrderStatus2GrtUrl;
    @Value("${grt.url.pushUpdateRecordToGrt}")
    private String pushUpdateRecordToGrt;
    @Value("${grt.url.orderReserveInfo}")
    private String orderReserveInfoUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private FlowInfoRepository flowInfoRepository;


    public R<List<OrderNoResultDto>> getGrtOrders(OrderNoListParamVo orderNoListParamVo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        String reqId = UuidUtil.generateUuid();
        String reqFrom = "PAD";
        String reqTime =  DateUtil.getNowDateStr();
        httpHeaders.add("reqId", reqId);
        httpHeaders.add("reqFrom",reqFrom);
        httpHeaders.add("reqTime", reqTime);
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
//        HttpEntity<String> requestEntity =
//                (HttpEntity<String>) tHttpEntity;

        String url = orderListUrl;
        if (Strings.isNotEmpty(orderNoListParamVo.getUserCode())){
            url = url.contains("?") ?
                    url.concat("&userCode=").concat(orderNoListParamVo.getUserCode())
                    : url.concat("?userCode=").concat(orderNoListParamVo.getUserCode());
        }
        if (Strings.isNotEmpty(orderNoListParamVo.getDealerCode())){
            url = url.contains("?") ?
                    url.concat("&dealerCode=").concat(orderNoListParamVo.getDealerCode())
                    : url.concat("?dealerCode=").concat(orderNoListParamVo.getDealerCode());
        }
        if (Strings.isNotEmpty(orderNoListParamVo.getUpdatedStart())){
            url = url.contains("?") ?
                    url.concat("&updatedStart=").concat(orderNoListParamVo.getUpdatedStart())
                    : url.concat("?updatedStart=").concat(orderNoListParamVo.getUpdatedStart());
        }
        if (Strings.isNotEmpty(orderNoListParamVo.getUpdatedEnd())){
            url = url.contains("?") ?
                    url.concat("&updatedEnd=").concat(orderNoListParamVo.getUpdatedEnd())
                    : url.concat("?updatedEnd=").concat(orderNoListParamVo.getUpdatedEnd());
        }
        if (Strings.isNotEmpty(orderNoListParamVo.getUpdatedEnd())){
            url = url.contains("?") ?
                    url.concat("&updatedEnd=").concat(orderNoListParamVo.getUpdatedEnd())
                    : url.concat("?updatedEnd=").concat(orderNoListParamVo.getUpdatedEnd());
        }
            url = url.contains("?") ?
                    url.concat("&pageNum=").concat(orderNoListParamVo.getPageNum()+"")
                    : url.concat("?pageNum=").concat(orderNoListParamVo.getPageNum()+"");
            url = url.contains("?") ?
                    url.concat("&pageSize=").concat(orderNoListParamVo.getPageSize()+"")
                    : url.concat("?pageSize=").concat(orderNoListParamVo.getPageSize()+"");
        ResponseEntity<OrderNoListResultDto> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, OrderNoListResultDto.class);

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


    public R grtGetOrderNoList(OrderNoListParamVo orderNoListParamVo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        String reqId = UuidUtil.generateUuid();
        String reqFrom = "PAD";
        String reqTime =  DateUtil.getNowDateStr();
        long timestamp = System.currentTimeMillis();
        String sign = GRTSignUtil.sign(GRTSignUtil.APP_KEY_GRT, timestamp, GRTSignUtil.SECRET_KEY_GRT);
        log.info("GRT请求路径URL --->>> {},时间戳为:{}",sign,timestamp);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        httpHeaders.add("reqId", reqId);
        httpHeaders.add("reqFrom",reqFrom);
        httpHeaders.add("reqTime", reqTime);
        httpHeaders.add("sign", sign);
        httpHeaders.add("appKey", GRTSignUtil.APP_KEY_GRT);
        httpHeaders.add("timestamp", String.valueOf(timestamp));
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        log.info("请求头为 --->>> {}", JSONObject.toJSONString(requestEntity));

        String url = grtConcatURL(orderNoListParamVo);
        log.info("GRT拼接后URL为 --->>> {}",url);

        ResponseEntity<OrderNoListResultDto> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, OrderNoListResultDto.class);
        log.info("GRT返回参数 --->>> {}", JSONObject.toJSONString(response));

        OrderNoListResultDto orderNoListResultDto = response.getBody();

        if (null == orderNoListResultDto || orderNoListResultDto.getStatus() == null){
            return R.fail("对接GRT获取待交车订单列表出错!  接口返回null! " );
        }else if (!orderNoListResultDto.getStatus().equals("200")){
            return R.fail("对接GRT获取待交车订单列表出错!  ".concat(orderNoListResultDto.getMessage() == null ? "接口返回null! " : orderNoListResultDto.getMessage()));
        }

        List<OrderNoResultDto> data = orderNoListResultDto.getData();
        data.forEach(s-> {
            s.setOrderStatus(GrtToPadEnums.getValueByVin(s.getOrderStatus(), s.getVin(), s.getBindStatus()));
            String valueByVin = GrtToPadEnums.getValueByVin(s.getOrderStatus(), s.getVin(), s.getBindStatus());
            System.out.println(valueByVin);
        });

        OrderNoListResultOutBo orderNoListResultOutBo = new OrderNoListResultOutBo();
        orderNoListResultOutBo.setData(data);
        orderNoListResultOutBo.setPageNum(orderNoListResultDto.getPageNum());
        orderNoListResultOutBo.setPageSize(orderNoListResultDto.getPageSize());
        orderNoListResultOutBo.setTotal(orderNoListResultDto.getTotal());

        return R.ok(orderNoListResultOutBo);
    }

    public String grtConcatURL(OrderNoListParamVo orderNoListParamVo) {

       String url = orderListUrl;
        if (Strings.isNotEmpty(orderNoListParamVo.getUserCode())){
            url = url.contains("?") ?
                    url.concat("&userCode=").concat(orderNoListParamVo.getUserCode())
                    : url.concat("?userCode=").concat(orderNoListParamVo.getUserCode());
        }
        if (Strings.isNotEmpty(orderNoListParamVo.getDealerCode())){
            url = url.contains("?") ?
                    url.concat("&dealerCode=").concat(orderNoListParamVo.getDealerCode())
                    : url.concat("?dealerCode=").concat(orderNoListParamVo.getDealerCode());
        }
        if (Strings.isNotEmpty(orderNoListParamVo.getUpdatedStart())){
            url = url.contains("?") ?
                    url.concat("&updatedStart=").concat(orderNoListParamVo.getUpdatedStart())
                    : url.concat("?updatedStart=").concat(orderNoListParamVo.getUpdatedStart());
        }
        if (Strings.isNotEmpty(orderNoListParamVo.getUpdatedEnd())){
            url = url.contains("?") ?
                    url.concat("&updatedEnd=").concat(orderNoListParamVo.getUpdatedEnd())
                    : url.concat("?updatedEnd=").concat(orderNoListParamVo.getUpdatedEnd());
        }
        //车主姓名
        if (Strings.isNotEmpty(orderNoListParamVo.getCustomerName())){
            url = url.contains("?") ?
                    url.concat("&customerName=").concat(orderNoListParamVo.getCustomerName())
                    : url.concat("?customerName=").concat(orderNoListParamVo.getCustomerName());
        }

        //车主手机号码 车主电话号码
        if (Strings.isNotEmpty(orderNoListParamVo.getMobilePhone())){
            url = url.contains("?") ?
                    url.concat("&customerPhone=").concat(orderNoListParamVo.getMobilePhone())
                    : url.concat("?customerPhone=").concat(orderNoListParamVo.getMobilePhone());
        }
        //订单状态 0   1   2  3  4  5
        if (Strings.isNotEmpty(orderNoListParamVo.getOrderStatus())){
            url = url.contains("?") ?
                    url.concat("&orderStatus=").concat(orderNoListParamVo.getOrderStatus())
                    : url.concat("?orderStatus=").concat(orderNoListParamVo.getOrderStatus());
        }

        //交车开始时间 - 结束时间
        if (Strings.isNotEmpty(orderNoListParamVo.getFinishHandoverDateStart())){
            url = url.contains("?") ?
                    url.concat("&deliveryTimeS=").concat(orderNoListParamVo.getReservationHandoverDateStart())
                    : url.concat("?deliveryTimeS=").concat(orderNoListParamVo.getReservationHandoverDateStart());
        }

        if (Strings.isNotEmpty(orderNoListParamVo.getFinishHandoverDateEnd())){
            url = url.contains("?") ?
                    url.concat("&deliveryTimeE=").concat(orderNoListParamVo.getReservationHandoverDateEnd())
                    : url.concat("?deliveryTimeE=").concat(orderNoListParamVo.getReservationHandoverDateEnd());
        }
        //是否超时
        if (Strings.isNotEmpty(orderNoListParamVo.getIsOverTime())){
            url = url.contains("?") ?
                    url.concat("&isTimedOut=").concat(orderNoListParamVo.getIsOverTime())
                    : url.concat("isTimedOut=").concat(orderNoListParamVo.getIsOverTime());
        }
        //销售开始结束-> 下定日期
        if (Strings.isNotEmpty(orderNoListParamVo.getSheetCreateDateStart())){
            url = url.contains("?") ?
                    url.concat("&salesDateS=").concat(orderNoListParamVo.getSheetCreateDateStart())
                    : url.concat("salesDateS=").concat(orderNoListParamVo.getSheetCreateDateStart());
        }
        //完成交车日期-> 下定日期
        if (Strings.isNotEmpty(orderNoListParamVo.getFinishHandoverDateStart())){
            url = url.contains("?") ?
                    url.concat("&finishTimeS=").concat(orderNoListParamVo.getFinishHandoverDateStart())
                    : url.concat("finishTimeS=").concat(orderNoListParamVo.getFinishHandoverDateStart());
        }

        if (Strings.isNotEmpty(orderNoListParamVo.getFinishHandoverDateEnd())){
            url = url.contains("?") ?
                    url.concat("&finishTimeE=").concat(orderNoListParamVo.getFinishHandoverDateEnd())
                    : url.concat("finishTimeE=").concat(orderNoListParamVo.getFinishHandoverDateEnd());
        }

        if (Strings.isNotEmpty(orderNoListParamVo.getSheetCreateDateEnd())){
            url = url.contains("?") ?
                    url.concat("&salesDateE=").concat(orderNoListParamVo.getSheetCreateDateEnd())
                    : url.concat("salesDateE=").concat(orderNoListParamVo.getSheetCreateDateEnd());
        }
        //分页
        url = url.contains("?") ?
                url.concat("&pageNum=").concat(orderNoListParamVo.getPageNum()+"")
                : url.concat("?pageNum=").concat(orderNoListParamVo.getPageNum()+"");
        url = url.contains("?") ?
                url.concat("&pageSize=").concat(orderNoListParamVo.getPageSize()+"")
                : url.concat("?pageSize=").concat(orderNoListParamVo.getPageSize()+"");
        return url;
    }


    public R<List<OrderDetailResultDto>> getGrtOrderDetail(String bussinessNo) {
        long timestamp = System.currentTimeMillis();
        String sign = GRTSignUtil.sign(GRTSignUtil.APP_KEY_GRT, timestamp, GRTSignUtil.SECRET_KEY_GRT);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("reqId", UuidUtil.generateUuid());
        httpHeaders.add("reqFrom","PAD");
        httpHeaders.add("reqTime", DateUtil.getNowDateStr());
        httpHeaders.add("sign", sign);
        httpHeaders.add("appKey", GRTSignUtil.APP_KEY_GRT);
        httpHeaders.add("timestamp", String.valueOf(timestamp));

        HttpEntity<String> requestEntity = new HttpEntity<>( httpHeaders);
        log.info("请求头为 --->>> {}", JSONObject.toJSONString(requestEntity));

        String url = String.format(orderDetailUrl,bussinessNo);
        String url1 = orderDetailUrl.concat("?bussinessNo=").concat(bussinessNo);
        log.info("url1 --->>> {}", JSONObject.toJSONString(url1));
        log.info("url --->>> {}", JSONObject.toJSONString(url));

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        OrderDetailListResultDto orderDetailListResultDto = JSONObject.parseObject(response.getBody(), OrderDetailListResultDto.class);
        log.info("查询交车确认信息 --->>> {}", JSONObject.toJSONString(response));

        if (orderDetailListResultDto == null || orderDetailListResultDto.getStatus() == null){
            return R.fail("对接GRT获取待交车订单详情出错!接口返回null  ");
        }else if (!"200".equals(orderDetailListResultDto.getStatus())){
            return R.fail("对接GRT获取待交车订单详情出错!  ".concat(orderDetailListResultDto.getMessage()));
        }
        return R.ok(orderDetailListResultDto.getData());
    }

    public R<List<OrderDetailResultDto>> getOrderDetailByNo(String bussinessNo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("reqId", UuidUtil.generateUuid());
        httpHeaders.add("reqFrom","PAD");
        httpHeaders.add("reqTime", DateUtil.getNowDateStr());
        HttpEntity<String> requestEntity = new HttpEntity<>( httpHeaders);

        String url = String.format(orderDetailUrl,bussinessNo);
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
            return R.fail( null == response.getBody() ?"null" : response.getBody().getMessage());
        }
        if (null == response.getBody() || "500".equals(response.getBody().getStatus())){
            return R.fail("调用GRT推送预约交车数据失败!  错误信息: "+(response.getBody()==null?"null":response.getBody().getMessage()));
        }
        return R.ok(null,response.getBody().getMessage());
    }

    public R updateGrtOrderDeliverDate(OrderDeliverDateParamVo orderDeliverDateParamVo) throws InterruptedException {

        long timestamp = System.currentTimeMillis();
        String sign = GRTSignUtil.sign(GRTSignUtil.APP_KEY_GRT, timestamp, GRTSignUtil.SECRET_KEY_GRT);
        Thread.sleep(800);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("reqId", UuidUtil.generateUuid());
        httpHeaders.add("reqFrom","PAD");
        httpHeaders.add("reqTime", DateUtil.getNowDateStr());
        httpHeaders.add("sign", sign);
        httpHeaders.add("appKey", GRTSignUtil.APP_KEY_GRT);
        httpHeaders.add("timestamp", String.valueOf(timestamp));
        HttpEntity<OrderDeliverDateParamVo> requestEntity = new HttpEntity<>(orderDeliverDateParamVo,httpHeaders);
        log.info("查询交车确认信息 --->>> {}", JSONObject.toJSONString(requestEntity.getHeaders()));

        ResponseEntity<BaseGrtResultDto> response = restTemplate.exchange(pushUpdateRecordToGrt, HttpMethod.POST, requestEntity, BaseGrtResultDto.class);
        if (response.getStatusCode() != HttpStatus.OK){
            return R.fail(response.getBody() == null?"null" : response.getBody().getMessage());
        }
        if (null == response.getBody() || "500".equals(response.getBody().getStatus())){
            return R.fail("调用GRT推送订单数据变更失败!  错误信息: "+(response.getBody()==null?"null":response.getBody().getMessage()));
        }
        return R.ok(null,response.getBody().getMessage());
    }

    @Transactional(rollbackFor = Exception.class)
    public R getPadOrderDetail(String bussinessNo) {
        Boolean result = false;
        OrderDetailOutBO orderDetailOutBO = new OrderDetailOutBO();
        R<List<OrderDetailResultDto>> grtOrderDetail = getGrtOrderDetail(bussinessNo);
        if(R.FAIL == grtOrderDetail.getCode()){
            throw new ServiceException(grtOrderDetail.getMsg(),grtOrderDetail.getCode());
        }
        List<OrderDetailResultDto> data = grtOrderDetail.getData();
        if (data.size() > 0){
            BeanUtils.copyProperties(data.get(0),orderDetailOutBO);
        }

        //查询数据库数据
            try {
                //幂等处理
                RedisLockUtils.lock(bussinessNo);
                //根据订单
                OrderDetail padOrderDetail = orderDetailRepository.getPadOrderDetail(bussinessNo);
                //获取当前操作厂端 与 TODO
                log.info("method:getPadOrderDetail(): PAD端订单查询订单号为：{}", bussinessNo);
                if(null == padOrderDetail){
                //订单信息入库
                result = orderDetailRepository.saveOrderDetailEntity(data.get(0),bussinessNo);
                if (!result){
                    throw new ServiceException("订单信息入库",500);
                }
                log.info("method:saveOrderDetailEntity().订单内容: {}", grtOrderDetail.getData());

                //新建交车流程信息并入库 -- 步骤为第一步 // TODO
                FlowInfoDto flowInfoDto = new FlowInfoDto();
                flowInfoDto.setBussinessNo(bussinessNo);
                flowInfoDto.setNodeNum(FlowNodeNum.ARRIVE_STORE.getCode());
                flowInfoDto.setVersion(0);
                result = flowInfoRepository.saveFlowInfoFirstNode(flowInfoDto);
                if (!result){
                    throw new ServiceException("流程接口入库失败",500);
                }
                orderDetailOutBO.setBussinessNo(bussinessNo);
                }else {
                    String remark = padOrderDetail.getRemark();
                    orderDetailOutBO.setRemark(remark);
                    orderDetailOutBO.setBussinessNo(bussinessNo);
                    return R.ok(orderDetailOutBO);
                }
            } finally {
                RedisLockUtils.unlock(bussinessNo);
            }

        return R.ok(orderDetailOutBO);
    }
}
