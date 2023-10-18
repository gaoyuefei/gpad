package com.gpad.gpadtool.controller;

import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.*;
import com.gpad.gpadtool.service.OrderDetailService;
import com.gpad.gpadtool.service.OrderNoService;
import com.gpad.gpadtool.service.OrderReserveService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderController.java
 * @Description 订单接口
 * @createTime 2023年09月22日 15:17:00
 */
@Slf4j
@Api(value = "订单接口", tags = "订单接口")
@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderNoService orderNoService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderReserveService orderReserveService;


    /**
     * 分页条件查询订单列表
     */
    @Operation(summary = "分页条件查询订单列表")
    @PostMapping("/listOrderByPage")
    public R<PageResult<OrderNoDto>> listOrderByPage(@RequestBody OrderListByPageVo orderListByPageVo){
        log.info("分页条件查询订单列表 --->>> {}", JSONObject.toJSONString(orderListByPageVo));
        PageResult<OrderNoDto> orderNoDtoPageResult = orderNoService.orderListByPage(orderListByPageVo);
        return R.ok(orderNoDtoPageResult);
    }

    /**
     * 条件查询订单列表
     */
    @Operation(summary = "条件查询订单列表")
    @PostMapping("/listOrderByParam")
    public R<List<OrderNoDto>> listOrderByParam(@RequestBody OrderListByPageVo orderListByPageVo){
        log.info("条件查询订单列表 --->>> {}", JSONObject.toJSONString(orderListByPageVo));
        List<OrderNoDto> orderNoDtoPageResult = orderNoService.listOrderByParam(orderListByPageVo);
        return R.ok(orderNoDtoPageResult);
    }


    /**
     * 查询订单详情
     */
    @Operation(summary = "查询订单详情")
    @GetMapping("/getOrderDetailByBusinessNo")
    public R<OrderDetailDto> getOrderDetailByBusinessNo(@RequestParam("businessNo") String businessNo){
        log.info("查询订单详情 --->>> {}", JSONObject.toJSONString(businessNo));
        OrderDetailDto orderDetailDto = orderDetailService.getOrderDetailByBusinessNo(businessNo);
        return R.ok(orderDetailDto);
    }

    /**
     * 新增/修改集合订单
     */
    @Operation(summary = "新增/修改集合订单")
    @PostMapping("/saveOrUpdateOrderSet")
    public R<List<OrderNoDto>> saveOrUpdateOrderSet(@RequestBody List<OrderNoDto> orderNoDtos){
        log.info("新增/修改集合订单 --->>> {}", JSONObject.toJSONString(orderNoDtos));
        orderNoDtos = orderNoService.saveOrUpdateOrderSet(orderNoDtos);
        return R.ok(orderNoDtos);
    }

    /**
     * 查询集合订单列表
     */
    @Operation(summary = "查询集合订单列表")
    @GetMapping("/getMergeOrderListByMergeId")
    public R<List<OrderNoDto>> getMergeOrderListByMergeId(@RequestParam("mergeId") String mergeId){
        log.info("查询集合订单列表 --->>> {}", JSONObject.toJSONString(mergeId));
        List<OrderNoDto> orderNoDtos = orderNoService.ListOrderByMergeId(mergeId);
        return R.ok(orderNoDtos);
    }


    /**
     * 新增/修改交车预约
     */
    @Operation(summary = "新增/修改交车预约")
    @PostMapping("/saveOrUpdateOrderReserveDto")
    public R<OrderReserveDto> saveOrUpdateOrderReserveDto(@RequestBody OrderReserveDto orderReserveDto){
        log.info("新增/修改交车预约 --->>> {}", JSONObject.toJSONString(orderReserveDto));
        orderReserveService.saveOrUpdateOrderReserveDto(orderReserveDto);
        return R.ok(orderReserveDto);
    }


    /**
     * 根据大客户订单客户姓名查询交车预约信息
     */
    @Operation(summary = "查询交车预约信息")
    @GetMapping("/getOrderReserveInfo")
    public R<OrderReserveDto> getOrderReserveInfo(@RequestParam("businessNo") String businessNo){
        log.info("查询交车预约信息 --->>> {}", JSONObject.toJSONString(businessNo));
        OrderReserveDto byBusinessNo = orderReserveService.getByBusinessNo(businessNo);
        return R.ok(byBusinessNo);
    }


    /**
     * 批量保存或更新交车订单信息
     */
    @Operation(summary = "批量保存或更新交车订单信息")
    @PostMapping("/batchSaveOrUpdateOrderNoList")
    public R<OrderReserveDto> batchSaveOrUpdateOrderNoList(@RequestBody List<OrderNoDto> list){
        log.info("批量保存或更新交车订单信息 --->>> {}", JSONObject.toJSONString(list));
        orderNoService.batchSaveOrUpdateOrderNoList(list);
        return R.ok();
    }

    /**
     * 批量保存或更新交车订单详情信息
     */
    @Operation(summary = "批量保存或更新交车订单详情信息")
    @PostMapping("/batchSaveOrUpdateOrderDetailList")
    public R<OrderReserveDto> batchSaveOrUpdateOrderDetailList(@RequestBody List<OrderDetailDto> list){
        log.info("批量保存或更新交车订单详情信息 --->>> {}", JSONObject.toJSONString(list));
        orderDetailService.batchSaveOrUpdateOrderDetailList(list);
        return R.ok();
    }


}
