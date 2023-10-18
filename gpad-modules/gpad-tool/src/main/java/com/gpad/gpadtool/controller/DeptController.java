package com.gpad.gpadtool.controller;

import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.DeptInfoDto;
import com.gpad.gpadtool.repository.DeptInfoRepository;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName DeptController.java
 * @Description TODO
 * @createTime 2023年09月25日 09:55:00
 */
@Slf4j
@Api(value = "部门接口", tags = "部门接口")
@RestController
@RequestMapping("/api/dept")
public class DeptController {

    @Autowired
    private DeptInfoRepository deptInfoRepository;


    /**
     * 查询全部部门信息
     */
    @Operation(summary = "查询全部部门信息")
    @GetMapping("/listDepts")
    public R<List<DeptInfoDto>> listDepts(){
        log.info("查询全部部门信息 --->>> ");
        return R.ok(deptInfoRepository.listAllDepts());
    }

    /**
     * 根据ID查部门信息
     */
    @Operation(summary = "根据ID查部门信息")
    @GetMapping("/findById")
    public R<DeptInfoDto> findById(Long id){
        log.info("根据ID查部门信息 --->>> {}", JSONObject.toJSONString(id));
        return R.ok(deptInfoRepository.findById(id));
    }

    /**
     * 新增部门信息
     */
    @Operation(summary = "新增部门信息")
    @PostMapping("/add")
    public R<DeptInfoDto> add(@RequestBody DeptInfoDto deptInfoDto){
        log.info("新增部门信息 --->>> {}",JSONObject.toJSONString(deptInfoDto));
        return R.ok(deptInfoRepository.add(deptInfoDto));
    }

    /**
     * 新增部门信息-批量
     */
    @Operation(summary = "新增部门信息-批量")
    @PostMapping("/addBatchDepts")
    public R<Void> addBatchDepts(@RequestBody List<DeptInfoDto> deptInfoDtos){
        log.info("新增部门信息-批量 --->>> {}",JSONObject.toJSONString(deptInfoDtos));
        deptInfoRepository.addBatchDepts(deptInfoDtos);
        return R.ok();
    }

    /**
     * 更新部门信息
     */
    @Operation(summary = "更新部门信息")
    @PostMapping("/updateById")
    public R<DeptInfoDto> updateById(@RequestBody DeptInfoDto deptInfoDto){
        log.info("更新部门信息 --->>> {}",JSONObject.toJSONString(deptInfoDto));
        return R.ok(deptInfoRepository.updateById(deptInfoDto));
    }

    /**
     * 删除部门信息
     */
    @Operation(summary = "删除部门信息")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody DeptInfoDto deptInfoDto){
        log.info("删除部门信息 --->>> {}",JSONObject.toJSONString(deptInfoDto));
        deptInfoRepository.delete(deptInfoDto);
        return R.ok();
    }

    /**
     * 删除部门信息
     */
    @Operation(summary = "删除部门信息")
    @GetMapping("/deleteById")
    public R<Void> deleteById(Long id){
        log.info("删除部门信息 --->>> {}",JSONObject.toJSONString(id));
        deptInfoRepository.deleteById(id);
        return R.ok();
    }
}
