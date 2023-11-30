package com.gpad.gpadtool.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.*;
import com.gpad.gpadtool.enums.PayMethodToCodeEnum;
import com.gpad.gpadtool.service.*;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName HandoverCarController.java
 * @Description TODO
 * @createTime 2023年09月22日 17:09:00
 */
@Slf4j
@Api(value = "对接GRT接口-交车页面接口相关", tags = "对接GRT接口-交车页面接口相关")
@RestController
@RequestMapping("/api")
public class HandoverCarController {
    @Autowired
    private HandoverCarPrepareService handoverCarPrepareService;

    @Autowired
    private HandoverCarCheckInfoService handoverCarCheckInfoService;

    @Autowired
    private HandoverCarJudgeService handoverCarJudgeService;

    @Autowired
    private HandoverCarService handoverCarService;

    @Autowired
    private GRTService grtService;


    /**
     * 查询流程节点信息
     */
    @Operation(summary = "查询流程节点信息")
    @GetMapping("/getProcessNodeByNo")
    public R<FlowInfoDto> queryProcessNode(@RequestParam("bussinessNo") String bussinessNo){
        log.info("查询交车评价信息 --->>> {}", bussinessNo);
        if (Strings.isEmpty(bussinessNo)){
            return R.fail("bussinessNo必传，请检查参数! ");
        }
        //TODO 嵌套切面跳转，加密解密
        FlowInfoDto flowInfoDto = handoverCarService.queryProcessNode(bussinessNo);
        log.info("查询流程节点信息 --->>> {}", JSON.toJSONString(flowInfoDto));
        return R.ok(flowInfoDto);
    }

    /**
     * 查询交车评价信息
     */
    @Operation(summary = "查询交车评价信息")
    @PostMapping("/getHandoverCarJudge")
    public R<HandoverCarJudgeDto> getHandoverCarJudge(@RequestBody HandoverCarJudgeDto handoverCarCheckInfoDto){
        log.info("查询交车评价信息 --->>> {}", JSONObject.toJSONString(handoverCarCheckInfoDto));
        if (Strings.isEmpty(handoverCarCheckInfoDto.getBussinessNo())){
            return R.fail("bussinessNo必传，请检查参数! ");
        }
        HandoverCarJudgeDto bussinessNo = handoverCarJudgeService.getBybussinessNo(handoverCarCheckInfoDto.getBussinessNo());
        return R.ok(bussinessNo);
    }


    /**
     * 查询交车确认信息
     */
    @Operation(summary = "查询交车确认信息")
    @PostMapping("/getHandoverCarCheckInfo")
    public R<HandoverCarCheckInfoOutBO> getHandoverCarCheckInfo(@RequestBody HandoverCarCheckInfoDto handoverCarCheckInfoDto){
        log.info("查询交车确认信息 --->>> {}", JSONObject.toJSONString(handoverCarCheckInfoDto));
        if (Strings.isEmpty(handoverCarCheckInfoDto.getBussinessNo())){
            return R.fail("bussinessNo必传，请检查参数! ");
        }

        //客户信息
        R<List<OrderDetailResultDto>> grtOrderDetail = grtService.getGrtOrderDetail(handoverCarCheckInfoDto.getBussinessNo());

        //合同信息
        HandoverCarCheckInfoOutBO handoverCarCheckInfoOutBO = handoverCarCheckInfoService.queryDeliverCarConfirmInfo(handoverCarCheckInfoDto);

        List<OrderDetailResultDto> data = grtOrderDetail.getData();
        if (!CollectionUtil.isEmpty(data)){
            handoverCarCheckInfoOutBO.setOrderDetailResultDto(grtOrderDetail.getData().get(0));
        }

        return R.ok(handoverCarCheckInfoOutBO);
    }

    /**
     * 白名单：H5
     */
    @Operation(summary = "查询交车白名单确认信息")
    @PostMapping("/H5/getHandoverCarCheckInfo")
    public R<HandoverCarCheckInfoOutBO> getHandoverCarCheckInfoH5(@RequestBody HandoverCarCheckInfoDto handoverCarCheckInfoDto){
        log.info("查询交车确认白名单信息 --->>> {}", JSONObject.toJSONString(handoverCarCheckInfoDto));
        if (Strings.isEmpty(handoverCarCheckInfoDto.getBussinessNo())){
            return R.fail("bussinessNo必传，请检查参数! ");
        }

        //客户信息
        R<List<OrderDetailResultDto>> grtOrderDetail = grtService.getGrtOrderDetail(handoverCarCheckInfoDto.getBussinessNo());

        //合同信息
        HandoverCarCheckInfoOutBO handoverCarCheckInfoOutBO = handoverCarCheckInfoService.queryDeliverCarConfirmInfo(handoverCarCheckInfoDto);

        List<OrderDetailResultDto> data = grtOrderDetail.getData();
        if (!CollectionUtil.isEmpty(data)){
            handoverCarCheckInfoOutBO.setOrderDetailResultDto(grtOrderDetail.getData().get(0));
        }

        return R.ok(handoverCarCheckInfoOutBO);
    }


    /**
     * 查询交车准备信息
     */
    @Operation(summary = "查询交车准备信息")
    @PostMapping("/getHandoverCarPrepareInfo")
    public R<HandoverCarPrepareOutBO> getHandoverCarPrepareInfo(@RequestBody HandoverCarPrepareDto handoverCarPrepareDto){
        log.info("查询交车准备信息 --->>> {}", JSONObject.toJSONString(handoverCarPrepareDto));
        if (Strings.isEmpty(handoverCarPrepareDto.getBussinessNo())){
            return R.fail("bussinessNo必传，请检查参数! ");
        }
        return R.ok(handoverCarPrepareService.queryReadyDeliverCarOrderNo(handoverCarPrepareDto));
    }

    /**
     * 保存交车准备信息
     */
    @Operation(summary = "保存交车准备信息")
    @PostMapping("/saveOrUpdateHandoverCarPrepare")
    public R<Boolean> saveOrUpdateHandoverCarPrepare(@RequestBody HandoverCarPrepareDto handoverCarPrepareDto){
        log.info("保存交车准备信息 --->>> {}", JSONObject.toJSONString(handoverCarPrepareDto));
        return handoverCarPrepareService.saveOrUpdateHandoverCarPrepareDto(handoverCarPrepareDto);
    }

    /**
     * 保存交车评价信息
     */
    @Operation(summary = "保存交车评价信息")
    @PostMapping("/saveHandoverCarJudge")
    public R<HandoverCarJudgeDto> saveHandoverCarJudge(@RequestBody HandoverCarJudgeDto handoverCarJudgeDto){
        log.info("保存交车评价信息 --->>> {}", JSONObject.toJSONString(handoverCarJudgeDto));
        handoverCarJudgeService.saveHandoverCarJudgeDto(handoverCarJudgeDto);
        return R.ok(handoverCarJudgeDto);
    }

    /**
     * 交车流程下一步接口(暂时不用)
     */
    @Operation(summary = "交车流程下一步接口")
    @PostMapping("/handOverCar")
    public R<Void> HandOverCar(@RequestBody HandoverCarDto handoverCarDto){
        log.info("交车流程下一步接口 --->>> {}", JSONObject.toJSONString(handoverCarDto));
        return handoverCarService.HandOverCar(handoverCarDto);
    }

    /**
     * 交车管理-车辆到店
     */
    @Operation(summary = "车辆到店")
    @PostMapping("/handOverCar/DeliveryOverCarNext")
    public R<Boolean> handOverCarNext(@RequestBody HandoverCarOutBO handoverCarOutBO){
        log.info("交车流程下一步接口 --->>> {}", JSONObject.toJSONString(handoverCarOutBO));
        return handoverCarService.HandOverCarNext(handoverCarOutBO);
    }

    /**
     * 03交车确认-合同信息保存
     */
    @Operation(summary = "保存交车确认信息")
    @PostMapping("/handOverCar/saveDeliverCarConfirmInfo")
    public R<Boolean> saveDeliverCarConfirmInfo(@RequestBody HandoverCarCheckInfoDto handoverCarCheckInfoDto){
        log.info("03交车确认-合同信息保存 --->>> {}", JSONObject.toJSONString(handoverCarCheckInfoDto));
        return handoverCarService.saveDeliverCarConfirmInfo(handoverCarCheckInfoDto);
    }

    /**
     * 02交车确认-交车仪式
     */
    @Operation(summary = "交车仪式")
    @PostMapping("/handOverCar/getDeliveryCeremonyPath")
    public R<FileInfoOutBo> getDeliveryCeremonyPath(@RequestBody DeliveryCeremonyInputBO deliveryCeremonyInputBO){
        log.info("02交车确认-交车仪式嵌套页面 --->>> {}", JSONObject.toJSONString(deliveryCeremonyInputBO));
        return handoverCarService.getDeliveryCeremonyPath(deliveryCeremonyInputBO);
    }

    /**
     * 上传图片查询-上传图片查询
     */
    @Operation(summary = "上传图片查询")
    @PostMapping("/handOverCar/H5/file")
    public R<FileInfoOutBo> getHandOverCarH5File(@RequestBody FileInfoInputBO fileInfoInputBO){
        return handoverCarService.getHandOverCarH5File(fileInfoInputBO);
    }

    /**
     * 交车完成接口
     */
    @Operation(summary = "交车完成接口")
    @PostMapping("/handOverCar/deliveryCompleted")
    public R<Boolean> deliveryCompletedNext(@RequestBody DeliveryCompletedInputBO deliveryCompletedInputBO){
        return handoverCarService.deliveryCompletedNext(deliveryCompletedInputBO);
    }

    /**
     * 查询合同连接查询
     */
    @Operation(summary = "查询合同连接查询")
    @PostMapping("/handOverCar/getContractLinkByBussinessNo")
    public R<String> getContractLinkByBussinessNo(@RequestBody DeliveryContractLinkInputBO deliveryContractLinkInputBO){
        return handoverCarService.getContractLinkByBussinessNo(deliveryContractLinkInputBO);
    }

}
