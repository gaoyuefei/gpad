package com.gpad.gpadtool.controller;

import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.FileInfoDto;
import com.gpad.gpadtool.repository.FileInfoRepository;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName FileInfoController.java
 * @Description 文件信息接口
 * @createTime 2023年09月25日 10:03:00
 */
@Slf4j
@Api(value = "文件信息接口", tags = "文件信息接口")
@RestController
@RequestMapping("/api")
public class FileInfoController {

    @Autowired
    private FileInfoRepository fileInfoRepository;


    /**
     * 批量变更或保存文件信息
     */
    @Operation(summary = "批量变更或保存文件信息")
    @PostMapping("/batchSaveOrUpdateFileInfoDtoList")
    public R<Void> batchSaveOrUpdateFileInfoDtoList(@RequestBody List<FileInfoDto> list){
        log.info("批量变更或保存用户信息 --->>> {}",JSONObject.toJSONString(list));
        fileInfoRepository.batchSaveOrUpdateFileInfoDtoList(list);
        return R.ok();
    }

    /**
     * 根据订单号和关联类型查文件列表
     */
    @Operation(summary = "根据订单号和关联类型查文件列表")
    @GetMapping("/getBybussinessNoAndLinkType")
    public R<List<FileInfoDto>> getBybussinessNoAndLinkType(@RequestParam("bussinessNo") String bussinessNo, @RequestParam("linkType") Integer linkType){
        log.info("根据订单号和关联类型查文件列表 --->>> bussinessNo = {}; linkType = {}", JSONObject.toJSONString(bussinessNo),linkType);
        List<FileInfoDto> result = fileInfoRepository.getBybussinessNoAndLinkType(bussinessNo,linkType);
        return R.ok(result);
    }

}
