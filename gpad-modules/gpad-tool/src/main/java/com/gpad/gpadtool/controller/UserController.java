package com.gpad.gpadtool.controller;

import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.UserInfoDto;
import com.gpad.gpadtool.repository.UserInfoRepository;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName UserController.java
 * @Description 用户接口
 * @createTime 2023年09月25日 10:54:00
 */
@Slf4j
@Api(value = "用户接口", tags = "用户接口。")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserInfoRepository userInfoRepository;


    /**
     * 查询全部用户信息
     */
    @Operation(summary = "查询全部用户信息")
    @GetMapping("/listUsers")
    public R<List<UserInfoDto>> listUsers(){
        log.info("查询全部用户信息 --->>> ");
        return R.ok(userInfoRepository.listAllUsers());
    }

    /**
     * 根据ID查用户信息
     */
    @Operation(summary = "根据ID查用户信息")
    @GetMapping("/findById")
    public R<UserInfoDto> findById(Long id){
        log.info("根据ID查用户信息 --->>> {}",JSONObject.toJSONString(id));
        return R.ok(userInfoRepository.findById(id));
    }

    /**
     * 新增用户信息
     */
    @Operation(summary = "新增用户信息")
    @PostMapping("/add")
    public R<UserInfoDto> add(@RequestBody UserInfoDto userInfoDto){
        log.info("新增用户信息 --->>> {}",JSONObject.toJSONString(userInfoDto));
        return R.ok(userInfoRepository.add(userInfoDto));
    }

    /**
     * 新增用户信息-批量
     */
    @Operation(summary = "新增用户信息-批量")
    @PostMapping("/addBatchUsers")
    public R<Void> addBatchUsers(@RequestBody List<UserInfoDto> userInfoDtos){
        log.info("新增用户信息-批量 --->>> {}",JSONObject.toJSONString(userInfoDtos));
        userInfoRepository.addBatchUsers(userInfoDtos);
        return R.ok();
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息")
    @PostMapping("/updateById")
    public R<UserInfoDto> updateById(@RequestBody UserInfoDto userInfoDto){
        log.info("更新用户信息 --->>> {}",JSONObject.toJSONString(userInfoDto));
        return R.ok(userInfoRepository.updateById(userInfoDto));
    }

    /**
     * 删除用户信息
     */
    @Operation(summary = "删除用户信息")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody UserInfoDto userInfoDto){
        log.info("删除用户信息 --->>> {}",JSONObject.toJSONString(userInfoDto));
        userInfoRepository.delete(userInfoDto);
        return R.ok();
    }

    /**
     * 删除用户信息
     */
    @Operation(summary = "删除用户信息")
    @GetMapping("/deleteById")
    public R<Void> deleteById(Long id){
        log.info("删除用户信息 --->>> {}",JSONObject.toJSONString(id));
        userInfoRepository.deleteById(id);
        return R.ok();
    }

    /**
     * 批量变更或保存用户信息
     */
    @Operation(summary = "批量变更或保存用户信息")
    @PostMapping("/saveOrUpdateByAccount")
    public R<Void> saveOrUpdateByAccount(@RequestBody List<UserInfoDto> userInfoDtos){
        log.info("批量变更或保存用户信息 --->>> {}",JSONObject.toJSONString(userInfoDtos));
        userInfoRepository.saveOrUpdateByAccount(userInfoDtos);
        return R.ok();
    }

    /**
     * 根据账号查询用户信息
     */
    @Operation(summary = "根据账号查询用户信息")
    @PostMapping("/findDtoByAccount")
    public R<UserInfoDto> findDtoByAccount(@RequestParam("account") String account ){
        log.info("根据账号查询用户信息 --->>> {}", JSONObject.toJSONString(account));
        UserInfoDto userInfoDto = userInfoRepository.findDtoByAccount(account);
        return R.ok(userInfoDto);
    }
}
