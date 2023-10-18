package com.gpad.gpadtool.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gpad.gpadtool.constant.SnowIdGenerator;
import com.gpad.gpadtool.domain.dto.HandoverCarCheckInfoDto;
import com.gpad.gpadtool.domain.entity.HandoverCarCheckInfo;
import com.gpad.gpadtool.repository.HandoverCarCheckInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName HandoverCarCheckInfoService.java
 * @Description TODO
 * @createTime 2023年09月22日 17:39:00
 */
@Service
public class HandoverCarCheckInfoService {
    @Autowired
    private HandoverCarCheckInfoRepository handoverCarCheckInfoRepository;

    public HandoverCarCheckInfoDto getByBusinessNo(String businessNo){
        LambdaQueryWrapper<HandoverCarCheckInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HandoverCarCheckInfo::getBusinessNo,businessNo);
        HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoRepository.getOne(wrapper);
        return JSONObject.parseObject(JSONObject.toJSONString(handoverCarCheckInfo), HandoverCarCheckInfoDto.class);
    }


    public HandoverCarCheckInfoDto saveHandoverCarCheckInfoDto(HandoverCarCheckInfoDto handoverCarCheckInfoDto) {
        handoverCarCheckInfoDto.setId(new SnowIdGenerator().nextId());
        handoverCarCheckInfoRepository.save(JSONObject.parseObject(JSONObject.toJSONString(handoverCarCheckInfoDto),HandoverCarCheckInfo.class));
        return handoverCarCheckInfoDto;
    }


    public void saveHandoverCarCheckInfoDtoList(List<HandoverCarCheckInfoDto> list) {
        List<HandoverCarCheckInfo> handoverCarCheckInfos = new ArrayList<>();
        list.forEach(l->handoverCarCheckInfos.add(JSONObject.parseObject(JSONObject.toJSONString(l),HandoverCarCheckInfo.class)));
        handoverCarCheckInfoRepository.saveBatch(handoverCarCheckInfos);
    }


    public void batchSaveOrUpdateHandoverCarCheckInfoDtoList(List<HandoverCarCheckInfoDto> list) {
        list.forEach(this::saveOrUpdateHandoverCarCheckInfoDto);
    }


    public void saveOrUpdateHandoverCarCheckInfoDto(HandoverCarCheckInfoDto handoverCarCheckInfoDto) {
        if (handoverCarCheckInfoDto.getId() == null || this.getByBusinessNo(handoverCarCheckInfoDto.getBusinessNo()) == null){
            saveHandoverCarCheckInfoDto(handoverCarCheckInfoDto);
        }
        updateById(handoverCarCheckInfoDto);
    }


    public HandoverCarCheckInfoDto updateById(HandoverCarCheckInfoDto handoverCarCheckInfoDto){
        handoverCarCheckInfoRepository.updateById(JSONObject.parseObject(JSONObject.toJSONString(handoverCarCheckInfoDto),HandoverCarCheckInfo.class));
        return handoverCarCheckInfoDto;
    }
}
