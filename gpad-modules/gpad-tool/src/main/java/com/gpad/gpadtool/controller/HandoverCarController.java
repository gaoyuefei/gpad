package com.gpad.gpadtool.controller;

import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.HandoverCarCheckInfoDto;
import com.gpad.gpadtool.domain.dto.HandoverCarDto;
import com.gpad.gpadtool.domain.dto.HandoverCarJudgeDto;
import com.gpad.gpadtool.domain.dto.HandoverCarPrepareDto;
import com.gpad.gpadtool.service.HandoverCarCheckInfoService;
import com.gpad.gpadtool.service.HandoverCarJudgeService;
import com.gpad.gpadtool.service.HandoverCarPrepareService;
import com.gpad.gpadtool.service.HandoverCarService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName HandoverCarController.java
 * @Description TODO
 * @createTime 2023年09月22日 17:09:00
 */
@Slf4j
@Api(value = "对接GRT接口", tags = "对接GRT接口")
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


    /**
     * 查询交车评价信息
     */
    @Operation(summary = "查询交车评价信息")
    @PostMapping("/getHandoverCarJudge")
    public R<HandoverCarJudgeDto> getHandoverCarJudge(@RequestBody HandoverCarJudgeDto handoverCarCheckInfoDto){
        log.info("查询交车评价信息 --->>> {}", JSONObject.toJSONString(handoverCarCheckInfoDto));
        if (Strings.isEmpty(handoverCarCheckInfoDto.getBusinessNo())){
            return R.fail("businessNo必传，请检查参数! ");
        }
        HandoverCarJudgeDto byBusinessNo = handoverCarJudgeService.getByBusinessNo(handoverCarCheckInfoDto.getBusinessNo());
        return R.ok(byBusinessNo);
    }


    /**
     * 查询交车确认信息
     */
    @Operation(summary = "查询交车确认信息")
    @PostMapping("/getHandoverCarCheckInfo")
    public R<HandoverCarCheckInfoDto> getHandoverCarCheckInfo(@RequestBody HandoverCarCheckInfoDto handoverCarCheckInfoDto){
        log.info("查询交车确认信息 --->>> {}", JSONObject.toJSONString(handoverCarCheckInfoDto));
        if (Strings.isEmpty(handoverCarCheckInfoDto.getBusinessNo())){
            return R.fail("businessNo必传，请检查参数! ");
        }
        HandoverCarCheckInfoDto byBusinessNo = handoverCarCheckInfoService.getByBusinessNo(handoverCarCheckInfoDto.getBusinessNo());
        return R.ok(byBusinessNo);
    }


    /**
     * 查询交车准备信息
     */
    @Operation(summary = "查询交车准备信息")
    @PostMapping("/getHandoverCarPrepareInfo")
    public R<HandoverCarPrepareDto> getHandoverCarPrepareInfo(@RequestBody HandoverCarPrepareDto handoverCarPrepareDto){
        log.info("查询交车准备信息 --->>> {}", JSONObject.toJSONString(handoverCarPrepareDto));
        if (Strings.isEmpty(handoverCarPrepareDto.getBusinessNo())){
            return R.fail("businessNo必传，请检查参数! ");
        }
        HandoverCarPrepareDto byBusinessNo = handoverCarPrepareService.getByBusinessNo(handoverCarPrepareDto.getBusinessNo());
        return R.ok(byBusinessNo);
    }

    /**
     * 新增/变更交车准备信息
     */
    @Operation(summary = "新增/变更交车准备信息")
    @PostMapping("/saveOrUpdateHandoverCarPrepare")
    public R<HandoverCarPrepareDto> saveOrUpdateHandoverCarPrepare(@RequestBody HandoverCarPrepareDto handoverCarPrepareDto){
        log.info("新增/变更交车准备信息 --->>> {}", JSONObject.toJSONString(handoverCarPrepareDto));
        handoverCarPrepareService.saveOrUpdateHandoverCarPrepareDto(handoverCarPrepareDto);
        return R.ok(handoverCarPrepareDto);
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
     * 交车流程下一步接口
     */
    @Operation(summary = "交车流程下一步接口")
    @PostMapping("/handOverCar")
    public R<Void> HandOverCar(@RequestBody HandoverCarDto handoverCarDto){
        log.info("交车流程下一步接口 --->>> {}", JSONObject.toJSONString(handoverCarDto));

        return handoverCarService.HandOverCar(handoverCarDto);
    }


}
