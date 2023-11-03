package com.gpad.gpadtool.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.constant.FileLinkType;
import com.gpad.gpadtool.constant.FileType;
import com.gpad.gpadtool.domain.dto.*;
import com.gpad.gpadtool.repository.FileInfoRepository;
import com.gpad.gpadtool.service.GRTService;
import com.gpad.gpadtool.service.OrderDetailService;
import com.gpad.gpadtool.service.OrderNoService;
import com.gpad.gpadtool.utils.SignUtil;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName GRTController.java
 * @Description 对接GRT接口
 * @createTime 2023年09月22日 10:58:00
 */
@Slf4j
@Api(value = "对接GRT接口", tags = "对接GRT接口")
@RestController
@RequestMapping("/api")
public class GRTController {
    @Value("${sign.secretKeys.grt}")
    private String grtSecretKey;

    @Autowired
    private GRTService grtService;

    @Autowired
    private OrderNoService orderNoService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private FileInfoRepository fileInfoRepository;


    /**
     * 获取GRT订单列表接口
     */
    @Operation(summary = "GRT-获取GRT订单列表接口")
    @PostMapping("/grt/getOrderNoList")
    public R getOrderNoList(@RequestBody OrderNoListParamVo orderNoListParamVo){
        // TODO 登录账号
        log.info("GRT-获取GRT订单列表接口 --->>> {}", JSONObject.toJSONString(orderNoListParamVo));
        return grtService.grtGetOrderNoList(orderNoListParamVo);
    }

    /**
     * GRT-待交车订单详情接口
     */
    @Operation(summary = "GRT-待交车订单详情接口")
    @PostMapping("/grt/getSyncOrderDetail")
    public R getGrtOrderDetail(@RequestParam("bussinessNo") String bussinessNo){
        log.info("GRT-待交车订单详情接口 --->>> {}", bussinessNo);
        return grtService.getPadOrderDetail(bussinessNo);
    }

    /**
     * GRT-待交车订单详情接口
     */
    @Operation(summary = "H5端GRT-待交车订单详情接口")
    @PostMapping("/grt/H5/getSyncOrderDetailH5")
    public R getSyncOrderDetailH5(@RequestParam("bussinessNo") String bussinessNo){
        log.info("GRT-待交车订单详情接口 --->>> {}", bussinessNo);
        return grtService.getPadOrderDetail(bussinessNo);
    }

    /**
     * GRT-修改计划交车日期
     */
    @Operation(summary = "GRT-修改计划交车日期")
    @PostMapping("/grt/updateGrtOrderDeliverDate")
    public R updateGrtOrderDeliverDate(@RequestBody OrderDeliverDateParamVo orderDeliverDateParamVo)  {
        log.info("GRT-修改计划交车日期 --->>> {}", JSON.toJSONString(orderDeliverDateParamVo));
        return grtService.updateGrtOrderDeliverDate(orderDeliverDateParamVo);
    }



    /**
     * 对接GRT推送订单状态变更
     */
    @Operation(summary = "对接GRT交车状态变更接口")
    @PostMapping("/out/grt/changeOrderStatus2Grt")
    public R<Void> changeOrderStatus2Grt(@RequestBody OrderStatusVo orderStatusVo){
        log.info("对接GRT推送订单状态变更 --->>> {}", JSONObject.toJSONString(orderStatusVo));
        return grtService.changeOrderStatus2Grt(orderStatusVo);
    }




























    /**
     * 同步待交车订单列表接口
     */
    @Operation(summary = "同步待交车订单列表接口")
    @PostMapping("/grt/syncOrderNoList")
    public void syncOrderNoList(@RequestBody OrderNoListParamVo orderNoListParamVo){
        log.info("GRT同步待交车订单列表接口 --->>> {}", JSONObject.toJSONString(orderNoListParamVo));
        R<List<OrderNoResultDto>> grtOrders = grtService.getGrtOrders(orderNoListParamVo);
        if (grtOrders.getCode() == R.SUCCESS){
            List<OrderNoDto> orderNoDtos = new ArrayList<>();
            List<OrderNoResultDto> responseBody = grtOrders.getData();
            responseBody.forEach(d->{
                OrderNoDto orderNoDto = JSONObject.parseObject(JSONObject.toJSONString(d), OrderNoDto.class);
                orderNoDto.setBussinessNo(d.getBussinessNo());
                orderNoDtos.add(orderNoDto);
            });
            orderNoService.batchSaveOrUpdateOrderNoList(orderNoDtos);
        }
    }

    /**
     * 同步待交车订单详情接口
     */
    @Operation(summary = "同步待交车订单详情接口")
    @PostMapping("/grt/syncOrderDetail")
    public void syncOrderDetail(@RequestParam("bussinessNo") String bussinessNo){
        log.info("GRT同步待交车订单详情接口 --->>> {}", bussinessNo);
        R<List<OrderDetailResultDto>> orderDetailByNo = grtService.getOrderDetailByNo(bussinessNo);
        if (orderDetailByNo.getCode() == R.SUCCESS) {
            List<OrderDetailResultDto> orderDetailResultDtos = orderDetailByNo.getData();
            List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
            orderDetailResultDtos.forEach(d->orderDetailDtos.add(JSONObject.parseObject(JSONObject.toJSONString(d),OrderDetailDto.class)));
            orderDetailDtos.forEach(o->o.setBussinessNo(bussinessNo));
            orderDetailService.batchSaveOrUpdateOrderDetailList(orderDetailDtos);

            List<FileInfoDto> fileInfoDtos = new ArrayList<>();
            orderDetailDtos.forEach(o->{
                o.getBillFiles().forEach(b->fileInfoDtos.add(new FileInfoDto(bussinessNo, FileLinkType.INVOICE.getCode(), b, "", FileType.INVOICE.getCode())));
            });

            //保存文件
            fileInfoRepository.batchSaveOrUpdateFileInfoDtoList(fileInfoDtos);
        }
    }


    /**
     * 查询待交车订单列表接口
     */
    @Operation(summary = "查询待交车订单列表接口")
    @PostMapping("/grt/getOrderNoListByUserCode")
    public R<List<OrderNoResultDto>> getOrderNoListByUserCode(@RequestBody OrderNoListParamVo orderNoListParamVo){
        log.info("GRT查询待交车订单列表接口 --->>> {}", JSONObject.toJSONString(orderNoListParamVo));
        return grtService.getGrtOrders(orderNoListParamVo);
    }


    /**
     * 查询待交车订单详情接口
     */
    @Operation(summary = "查询待交车订单详情接口")
    @PostMapping("/grt/getOrderDetailBybussinessNo")
    public R<List<OrderDetailResultDto>> getOrderDetailBybussinessNo(@RequestParam("bussinessNo") String bussinessNo){
        log.info("GRT查询待交车订单详情接口 --->>> {}", bussinessNo);
        return grtService.getOrderDetailByNo(bussinessNo);
    }

    /**
     *  对接GRT接收订单状态变更
     */
    @Operation(summary = "对接GRT接收订单状态变更")
    @PostMapping("/out/grt/changeOrderStatus")
    public ChangeOrderStatusResultDto changeOrderStatus(HttpServletRequest request, @RequestBody ChangeOrderStatusVo changeOrderStatusVo){
        log.info("对接GRT接收订单状态变更 --->>> {}", JSONObject.toJSONString(changeOrderStatusVo));
        String reqId = request.getHeader("reqId");
        log.info("reqId   ===   {}",reqId);
        String errorMsg = SignUtil.checkSign(request, grtSecretKey,changeOrderStatusVo);
        if (Strings.isNotEmpty(errorMsg)){
            return new ChangeOrderStatusResultDto("500",errorMsg);
        }

        //fixme 变更订单状态


        return new ChangeOrderStatusResultDto("200",null);
    }

    /**
     * 对接GRT推送预约交车信息
     */
    @Operation(summary = "对接GRT推送预约交车信息")
    @PostMapping("/out/grt/orderReserveInfo")
    public R<Void> orderReserveInfo(@RequestBody OrderReserveVo orderReserveVo){
        log.info("对接GRT推送预约交车信息 --->>> {}", JSONObject.toJSONString(orderReserveVo));
        return grtService.orderReserveInfo(orderReserveVo);
    }
}
