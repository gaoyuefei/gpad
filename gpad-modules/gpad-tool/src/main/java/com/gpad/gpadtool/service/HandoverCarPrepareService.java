package com.gpad.gpadtool.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gpad.gpadtool.domain.entity.HandoverCarPrepare;
import com.gpad.gpadtool.domain.dto.HandoverCarPrepareDto;
import com.gpad.gpadtool.repository.HandoverCarPrepareRepository;
import com.gpad.gpadtool.utils.UuidUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName HandoverCarPrepareService.java
 * @Description TODO
 * @createTime 2023年09月22日 17:18:00
 */
@Service
public class HandoverCarPrepareService {
    @Autowired
    private HandoverCarPrepareRepository handoverCarPrepareRepository;

    public HandoverCarPrepareDto getByBusinessNo(String businessNo){
        LambdaQueryWrapper<HandoverCarPrepare> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HandoverCarPrepare::getBusinessNo,businessNo);
        HandoverCarPrepare handoverCarPrepare = handoverCarPrepareRepository.getOne(wrapper);
        return JSONObject.parseObject(JSONObject.toJSONString(handoverCarPrepare), HandoverCarPrepareDto.class);
    }

    public HandoverCarPrepareDto saveHandoverCarPrepareDto(HandoverCarPrepareDto handoverCarPrepareDto) {
        handoverCarPrepareDto.setId(UuidUtil.generateUuid());
        handoverCarPrepareRepository.save(JSONObject.parseObject(JSONObject.toJSONString(handoverCarPrepareDto), HandoverCarPrepare.class));
        return handoverCarPrepareDto;
    }

    public void saveHandoverCarPrepareDtoList(List<HandoverCarPrepareDto> list) {
        List<HandoverCarPrepare> handoverCarPrepares = new ArrayList<>();
        list.forEach(l->handoverCarPrepares.add(JSONObject.parseObject(JSONObject.toJSONString(l),HandoverCarPrepare.class)));
        handoverCarPrepareRepository.saveBatch(handoverCarPrepares);
    }

    public void batchSaveOrUpdateHandoverCarPrepareDtoList(List<HandoverCarPrepareDto> list) {
        list.forEach(this::saveOrUpdateHandoverCarPrepareDto);
    }

    public void saveOrUpdateHandoverCarPrepareDto(HandoverCarPrepareDto handoverCarPrepareDto) {
        if (Strings.isBlank(handoverCarPrepareDto.getId()) || this.getByBusinessNo(handoverCarPrepareDto.getBusinessNo()) == null){
            saveHandoverCarPrepareDto(handoverCarPrepareDto);
        }
        updateById(handoverCarPrepareDto);
    }

    public HandoverCarPrepareDto updateById(HandoverCarPrepareDto handoverCarPrepareDto){
        handoverCarPrepareRepository.updateById(JSONObject.parseObject(JSONObject.toJSONString(handoverCarPrepareDto),HandoverCarPrepare.class));
        return handoverCarPrepareDto;
    }
}
