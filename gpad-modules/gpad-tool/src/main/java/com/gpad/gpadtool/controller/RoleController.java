package com.gpad.gpadtool.controller;

import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.RoleInfoDto;
import com.gpad.gpadtool.repository.RoleInfoRepository;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName RoleController.java
 * @Description 角色接口
 * @createTime 2023年09月25日 11:15:00
 */
@Slf4j
@Api(value = "角色接口", tags = "角色接口")
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleInfoRepository roleInfoRepository;


    /**
     * 查询全部角色信息
     */
    @Operation(summary = "查询全部角色信息")
    @GetMapping("/listRoles")
    public R<List<RoleInfoDto>> listRoles(){
        log.info("查询全部角色信息 --->>> ");
        return R.ok(roleInfoRepository.listAllRoles());
    }

    /**
     * 根据ID查角色信息
     */
    @Operation(summary = "根据ID查角色信息")
    @GetMapping("/findById")
    public R<RoleInfoDto> findById(Long id){
        log.info("根据ID查角色信息 --->>> {}",JSONObject.toJSONString(id));
        return R.ok(roleInfoRepository.findById(id));
    }

    /**
     * 新增角色信息
     */
    @Operation(summary = "新增角色信息")
    @PostMapping("/add")
    public R<RoleInfoDto> add(@RequestBody RoleInfoDto roleInfoDto){
        log.info("新增角色信息 --->>> {}",JSONObject.toJSONString(roleInfoDto));
        return R.ok(roleInfoRepository.add(roleInfoDto));
    }

    /**
     * 新增角色信息-批量
     */
    @Operation(summary = "新增角色信息-批量")
    @PostMapping("/addBatchRoles")
    public R<Void> addBatchRoles(@RequestBody List<RoleInfoDto> roleInfoDtos){
        log.info("新增角色信息-批量 --->>> {}",JSONObject.toJSONString(roleInfoDtos));
        roleInfoRepository.addBatchRoles(roleInfoDtos);
        return R.ok();
    }

    /**
     * 更新角色信息
     */
    @Operation(summary = "更新角色信息")
    @PostMapping("/updateById")
    public R<RoleInfoDto> updateById(@RequestBody RoleInfoDto roleInfoDto){
        log.info("更新角色信息 --->>> {}",JSONObject.toJSONString(roleInfoDto));
        return R.ok(roleInfoRepository.updateById(roleInfoDto));
    }

    /**
     * 删除角色信息
     */
    @Operation(summary = "删除角色信息")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody RoleInfoDto roleInfoDto){
        log.info("删除角色信息 --->>> {}",JSONObject.toJSONString(roleInfoDto));
        roleInfoRepository.delete(roleInfoDto);
        return R.ok();
    }

    /**
     * 删除角色信息
     */
    @Operation(summary = "删除角色信息")
    @GetMapping("/deleteById")
    public R<Void> deleteById(Long id){
        log.info("删除角色信息 --->>> {}", JSONObject.toJSONString(id));
        roleInfoRepository.deleteById(id);
        return R.ok();
    }
}
