package com.gpad.gpadtool.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.gpadtool.constant.CommCode;
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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author Donald.Lee
 * @version 1.0.1
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
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            log.info("GRT返回参数 --->>> response{}", JSONObject.toJSONString(response));
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        stopWatch.stop();

        if (ObjectUtil.isEmpty(response)){
            return R.ok(null,"查无数据");
        }
        if (StringUtils.isEmpty(response.getBody())){
            return R.ok(null,"查无数据");
        }
        OrderNoListResultDto orderNoListResultDto = null;
        try {
            orderNoListResultDto = JSONObject.parseObject(response.getBody(), OrderNoListResultDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("转换后参数为try --->>> orderNoListResultDto{}", JSONObject.toJSONString(orderNoListResultDto));

        if (null == orderNoListResultDto || orderNoListResultDto.getStatus() == null){
//            return R.fail("对接GRT获取待交车订单列表出错!  接口返回null! " );
            return R.ok(null,"查无数据");
        }else if (!"200".equals(orderNoListResultDto.getStatus())){
//            return R.fail("对接GRT获取待交车订单列表出错!  ".concat(orderNoListResultDto.getMessage() == null ? "接口返回null! " : orderNoListResultDto.getMessage()));
            return R.ok(null,"查无数据");
        }
        stopWatch.start();
        List<OrderNoResultDto> data = orderNoListResultDto.getData();
        data.forEach(s-> {
            log.info("打印转换参数为code码{},VIN{},BindStatus{}",s.getOrderStatus(), s.getVin(),s.getBindStatus());
            s.setOrderStatus(GrtToPadEnums.getValueByVin(s.getOrderStatus(), s.getVin(), s.getBindStatus()));
        });
//        data.sort((o1, o2) -> o2.getDeliveringDate().compareTo(o1.getDeliveringDate()));
//        log.info("GRT订单列表返回数据为{}",JSON.toJSONString(data));
        OrderNoListResultOutBo orderNoListResultOutBo = new OrderNoListResultOutBo();
        orderNoListResultOutBo.setData(data);
        orderNoListResultOutBo.setPageNum(orderNoListResultDto.getPageNum());
        orderNoListResultOutBo.setPageSize(orderNoListResultDto.getPageSize());
        orderNoListResultOutBo.setTotal(orderNoListResultDto.getTotal());
        stopWatch.stop();
        log.info("统计耗时{}",stopWatch.prettyPrint());
        return R.ok(orderNoListResultOutBo);
    }

    public String grtConcatURL(OrderNoListParamVo orderNoListParamVo) {

       String url = orderListUrl;
        if (Strings.isNotEmpty(orderNoListParamVo.getUserCode())){
            url = url.contains("?") ?
                    url.concat("&userCode=").concat(orderNoListParamVo.getUserCode())
                    : url.concat("?userCode=").concat(orderNoListParamVo.getUserCode());
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
        //订单状态 0   1   2  3  4 已完成  5
        if (Strings.isNotEmpty(orderNoListParamVo.getOrderStatus())){
            url = url.contains("?") ?
                    url.concat("&orderStatus=").concat(orderNoListParamVo.getOrderStatus())
                    : url.concat("?orderStatus=").concat(orderNoListParamVo.getOrderStatus());
        }

        //交车开始时间 - 结束时间
        if (Strings.isNotEmpty(orderNoListParamVo.getReservationHandoverDateStart())){
            url = url.contains("?") ?
                    url.concat("&deliveryTimeS=").concat(orderNoListParamVo.getReservationHandoverDateStart())
                    : url.concat("?deliveryTimeS=").concat(orderNoListParamVo.getReservationHandoverDateStart());
        }

        if (Strings.isNotEmpty(orderNoListParamVo.getReservationHandoverDateEnd())){
            url = url.contains("?") ?
                    url.concat("&deliveryTimeE=").concat(orderNoListParamVo.getReservationHandoverDateEnd())
                    : url.concat("?deliveryTimeE=").concat(orderNoListParamVo.getReservationHandoverDateEnd());
        }
        //是否超时
        if (Strings.isNotEmpty(orderNoListParamVo.getIsOverTime())){
            url = url.contains("?") ?
                    url.concat("&isTimedOut=").concat(orderNoListParamVo.getIsOverTime())
                    : url.concat("?isTimedOut=").concat(orderNoListParamVo.getIsOverTime());
        }
        //销售开始结束-> 下定日期
        if (Strings.isNotEmpty(orderNoListParamVo.getSheetCreateDateStart())){
            url = url.contains("?") ?
                    url.concat("&salesDateS=").concat(orderNoListParamVo.getSheetCreateDateStart())
                    : url.concat("?salesDateS=").concat(orderNoListParamVo.getSheetCreateDateStart());
        }
        //完成交车日期-> 下定日期
        if (Strings.isNotEmpty(orderNoListParamVo.getFinishHandoverDateStart())){
            url = url.contains("?") ?
                    url.concat("&finishTimeS=").concat(orderNoListParamVo.getFinishHandoverDateStart())
                    : url.concat("?finishTimeS=").concat(orderNoListParamVo.getFinishHandoverDateStart());
        }

        if (Strings.isNotEmpty(orderNoListParamVo.getFinishHandoverDateEnd())){
            url = url.contains("?") ?
                    url.concat("&finishTimeE=").concat(orderNoListParamVo.getFinishHandoverDateEnd())
                    : url.concat("?finishTimeE=").concat(orderNoListParamVo.getFinishHandoverDateEnd());
        }

        if (Strings.isNotEmpty(orderNoListParamVo.getSheetCreateDateEnd())){
            url = url.contains("?") ?
                    url.concat("&salesDateE=").concat(orderNoListParamVo.getSheetCreateDateEnd())
                    : url.concat("?salesDateE=").concat(orderNoListParamVo.getSheetCreateDateEnd());
        }
        //销售店代码
        if (Strings.isNotEmpty(orderNoListParamVo.getDealerCode())){
            url = url.contains("?") ?
                    url.concat("&dealerCode=").concat(orderNoListParamVo.getDealerCode())
                    : url.concat("?dealerCode=").concat(orderNoListParamVo.getDealerCode());
        }
        //销售店代码
        if (Strings.isNotEmpty(orderNoListParamVo.getBussinessNo())){
            url = url.contains("?") ?
                    url.concat("&bussinessNo=").concat(orderNoListParamVo.getBussinessNo())
                    : url.concat("?bussinessNo=").concat(orderNoListParamVo.getBussinessNo());
        }
        //销售店代码
        if (Strings.isNotEmpty(orderNoListParamVo.getOriginOrderNo())){
            url = url.contains("?") ?
                    url.concat("&originOrderNo=").concat(orderNoListParamVo.getOriginOrderNo())
                    : url.concat("?originOrderNo=").concat(orderNoListParamVo.getOriginOrderNo());
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

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        stopWatch.stop();
        log.info("GRT详情接口耗时{}",stopWatch.prettyPrint());
        if (ObjectUtil.isEmpty(response)){
            return R.fail(null,"查无数据");
        }
        log.info("订单详情接口 --->>> response{}", JSONObject.toJSONString(response.getBody()));

        OrderDetailListResultDto orderDetailListResultDto = JSONObject.parseObject(response.getBody(), OrderDetailListResultDto.class);
        log.info("订单详情接口 --->>> response{}", JSONObject.toJSONString(response));

        if (orderDetailListResultDto == null || orderDetailListResultDto.getStatus() == null){
            return R.fail(null,"查无数据");
        }else if (!"200".equals(orderDetailListResultDto.getStatus())){
            return R.fail(null,"查无数据");
        }
        log.info("订单详情接口 --->>> response{}", JSONObject.toJSONString(response.getBody()));
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

    public R changeOrderStatus2Grt(OrderStatusVo orderStatusVo){
        log.info("method:changeOrderStatus2Grt() --->>>打印GRT入参为 url为{}", changeOrderStatus2GrtUrl);
        long timestamp = System.currentTimeMillis();
        String sign = GRTSignUtil.sign(GRTSignUtil.APP_KEY_GRT, timestamp, GRTSignUtil.SECRET_KEY_GRT);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("reqId", UuidUtil.generateUuid());
        httpHeaders.add("reqFrom","PAD");
        httpHeaders.add("reqTime", DateUtil.getNowDateStr());
        httpHeaders.add("sign", sign);
        httpHeaders.add("appKey", GRTSignUtil.APP_KEY_GRT);
        httpHeaders.add("timestamp", String.valueOf(timestamp));
        HttpEntity<OrderStatusVo> requestEntity = new HttpEntity<>(orderStatusVo,httpHeaders);
        log.info("查询交车确认信息 --->>> {}", JSONObject.toJSONString(requestEntity));
        log.info("查询交车确认信息 --->>> {}", JSONObject.toJSONString(requestEntity.getHeaders()));

        ResponseEntity<BaseGrtResultDto> response = null;
        try {
            response = restTemplate.exchange(changeOrderStatus2GrtUrl, HttpMethod.POST, requestEntity, BaseGrtResultDto.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        if (ObjectUtil.isEmpty(response)){
            return R.fail(null, "状态更新失败，请检查交车流程是否已完成");
        }
        if (response.getStatusCode() != HttpStatus.OK){
            return R.fail(null,"网络波动异常,请联系管理");
        }
        if (response.getBody()==null || response.getBody().getStatus().equals("500")){
            return R.fail(null ,"网络波动异常,请联系管理");
        }
        log.info("method:changeOrderStatus2Grt() --->>> 方法执行结束{}",JSONObject.toJSONString(response.getBody()));
        String message = response.getBody().getMessage();
        log.info("method:changeOrderStatus2Grt() --->>> 打印GRT真实报错{}",JSONObject.toJSONString(message));
        return R.ok(null, "状态更新成功");
    }

    public R orderReserveInfo(OrderReserveVo orderReserveVo){

        ResponseEntity<BaseGrtResultDto> response = restTemplate.exchange(orderReserveInfoUrl, HttpMethod.POST, new HttpEntity<>(orderReserveVo), BaseGrtResultDto.class);
        if (response.getStatusCode() != HttpStatus.OK){
            return R.fail( null == response.getBody() ?"null" : response.getBody().getMessage());
        }
        if (null == response.getBody() || "500".equals(response.getBody().getStatus())){
            return R.fail("调用GRT推送预约交车数据失败!  错误信息: "+(response.getBody()==null?"null":response.getBody().getMessage()));
        }
        return R.ok(null,response.getBody().getMessage());
    }

    public R updateGrtOrderDeliverDate(OrderDeliverDateParamVo orderDeliverDateParamVo) {
        log.info("进入 method：updateGrtOrderDeliverDate： --->>> url为{}", pushUpdateRecordToGrt);
        long timestamp = System.currentTimeMillis();
        String sign = GRTSignUtil.sign(GRTSignUtil.APP_KEY_GRT, timestamp, GRTSignUtil.SECRET_KEY_GRT);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("reqId", UuidUtil.generateUuid());
        httpHeaders.add("reqFrom","PAD");
        httpHeaders.add("reqTime", DateUtil.getNowDateStr());
        httpHeaders.add("sign", sign);
        httpHeaders.add("appKey", GRTSignUtil.APP_KEY_GRT);
        httpHeaders.add("timestamp", String.valueOf(timestamp));
        HttpEntity<OrderDeliverDateParamVo> requestEntity = new HttpEntity<>(orderDeliverDateParamVo,httpHeaders);
        log.info("封装请求头为 new HttpEntity<>(orderDeliverDateParamVo,httpHeaders)()--->>> {}", JSONObject.toJSONString(requestEntity));

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(pushUpdateRecordToGrt, HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        log.info("修改预计交车时间 --->>> response为{}", JSONObject.toJSONString(response));
        if (ObjectUtil.isEmpty(response)){
            return R.fail(null,"订单状态修改失败");
        }
        log.info("封装请求头为 --->>> response{}", JSONObject.toJSONString(response));
        try {
            log.info("修改预计交车时间 --->>> body{}", JSONObject.toJSONString(response.getBody()));
            log.info("修改预计交车时间 --->>> StatusCode{}", JSONObject.toJSONString(response.getStatusCode()));
            log.info("修改预计交车时间 --->>> StatusCodeValue{}", JSONObject.toJSONString(response.getStatusCodeValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (response.getStatusCode() != HttpStatus.OK){
//            return R.fail(response.getBody() == null?"null" : response.getBody().getMessage());
//        }
//        if (null == response.getBody() || "500".equals(response.getBody().getStatus())){
//            return R.fail("调用GRT推送订单数据变更失败!  错误信息: "+(response.getBody()==null?"null":response.getBody().getMessage()));
//        }

        log.info("进入 method：updateGrtOrderDeliverDate： --->>> 执行结束{}", response.getBody());
        return R.ok(null,"修改订单状态成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public R getPadOrderDetail(String bussinessNo) {
        Boolean result = false;
        OrderDetailOutBO orderDetailOutBO = new OrderDetailOutBO();
        R<List<OrderDetailResultDto>> grtOrderDetail = getGrtOrderDetail(bussinessNo);
        if(R.FAIL == grtOrderDetail.getCode()){
//            throw new ServiceException(grtOrderDetail.getMsg(),grtOrderDetail.getCode());
            return R.ok(null,"查无数据");
        }
        log.info("GRT-待交车订单详情接口method：getGrtOrderDetail()调用结束 --->>>{}", JSON.toJSONString(orderDetailOutBO));
        List<OrderDetailResultDto> data = grtOrderDetail.getData();
        log.info("GRT：在PAD 调用判断条件--->>>{}  条件2{}", JSON.toJSONString(data),CollectionUtil.isEmpty(data));
        if (data.size() <= 0 || CollectionUtil.isEmpty(data)){
//           throw new ServiceException("查询订单信息有误", CommCode.DATA_NOT_FOUND.getCode());
            return R.ok(null,"查无数据");
        }
        OrderDetailResultDto source = data.get(0);
        BeanUtils.copyProperties(source,orderDetailOutBO);
        String grtNo = source.getBussinessNo();
        if (StringUtils.isEmpty(grtNo)){
            throw new ServiceException("GRT订单列表与GRT订单详情不一致，请系统联系管理员", CommCode.DATA_NOT_FOUND.getCode());
        }
        //查询数据库数据
            try {
                //幂等处理
                RedisLockUtils.lock(bussinessNo);
                log.info("锁开始 --》》》》{}", bussinessNo);
                //根据订单
                OrderDetail padOrderDetail = orderDetailRepository.getPadOrderDetail(bussinessNo);
                //获取当前操作厂端 与 TODO
                log.info("method:getPadOrderDetail(): PAD端订单查询订单号为：{}", bussinessNo);
                if(ObjectUtil.isEmpty(padOrderDetail)||null == padOrderDetail){
                log.info("method:getPadOrderDetail(): 进入判断条件内：{}", com.alibaba.fastjson2.JSONObject.toJSONString(padOrderDetail));
                //订单信息入库
                result = orderDetailRepository.saveOrderDetailEntity(data.get(0),bussinessNo);
                if (!result){
                    throw new ServiceException("订单信息入库失败",CommCode.DATA_UPDATE_WRONG.getCode());
                }
                log.info("method:saveOrderDetailEntity().订单内容: {}", grtOrderDetail.getData());
                //新建交车流程信息并入库 -- 步骤为第一步
                FlowInfoDto flowInfoDto = new FlowInfoDto();
                flowInfoDto.setBussinessNo(bussinessNo);
                flowInfoDto.setNodeNum(FlowNodeNum.ARRIVE_STORE.getCode());
                flowInfoDto.setVersion(0);
                result = flowInfoRepository.saveFlowInfoFirstNode(flowInfoDto);
                if (!result){
                    throw new ServiceException("流程接口入库失败",CommCode.DATA_UPDATE_WRONG.getCode());
                }
                orderDetailOutBO.setBussinessNo(bussinessNo);
                }else {
                    log.info("查到数据走这里时间");
                    String remark = padOrderDetail.getRemark();
                    orderDetailOutBO.setRemark(remark);
                    orderDetailOutBO.setBussinessNo(bussinessNo);
                    return R.ok(orderDetailOutBO);
                }
            } finally {
                RedisLockUtils.unlock(bussinessNo);
                log.info("锁结束 --》》》》{}", bussinessNo);
            }

        return R.ok(orderDetailOutBO);
    }
}
