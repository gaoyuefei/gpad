package com.gpad.gpadtool.repository;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.domain.dto.FlowInfoDto;
import com.gpad.gpadtool.domain.entity.FlowInfo;
import com.gpad.gpadtool.mapper.FlowInfoMapper;
import com.gpad.gpadtool.utils.UuidUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName FlowInfoRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 17:57:00
 */
@Service
public class FlowInfoRepository extends ServiceImpl<FlowInfoMapper, FlowInfo> {
    
    public FlowInfoDto saveFlowInfoDto(FlowInfoDto flowInfoDto) {
        flowInfoDto.setId(UuidUtil.generateUuid());
//        flowInfoDto.setCreateBy();
        flowInfoDto.setCreateTime(new Date());
        this.save(JSONObject.parseObject(JSONObject.toJSONString(flowInfoDto),FlowInfo.class));
        return flowInfoDto;
    }

    
    public FlowInfoDto getByBusinessNo(String businessNo) {
        LambdaQueryWrapper<FlowInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FlowInfo::getBusinessNo,businessNo);
        FlowInfo flowInfo = this.getOne(wrapper);
        return JSONObject.parseObject(JSONObject.toJSONString(flowInfo),FlowInfoDto.class);
    }

    
    public void saveFlowInfoDtoList(List<FlowInfoDto> list) {
        List<FlowInfo> flowInfos = new ArrayList<>();
        list.forEach(l->flowInfos.add(JSONObject.parseObject(JSONObject.toJSONString(l),FlowInfo.class)));
        this.saveBatch(flowInfos);
    }

    
    public void batchSaveOrUpdateFlowInfoDtoList(List<FlowInfoDto> list) {
        list.forEach(this::saveOrUpdateFlowInfoDto);
    }

    
    public void saveOrUpdateFlowInfoDto(FlowInfoDto flowInfoDto) {
        /*if (byBusinessNo==null){
            saveFlowInfoDto(flowInfoDto);
        }else {
            updateById(flowInfoDto);
        }*/
    }

    
    public FlowInfoDto updateById(FlowInfoDto flowInfoDto){
        flowInfoDto.setUpdateTime(new Date());
        this.updateById(JSONObject.parseObject(JSONObject.toJSONString(flowInfoDto),FlowInfo.class));
        return flowInfoDto;
    }
}
