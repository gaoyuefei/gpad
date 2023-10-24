package com.gpad.gpadtool.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gpad.gpadtool.constant.SnowIdGenerator;
import com.gpad.gpadtool.domain.dto.HandoverCarJudgeDto;
import com.gpad.gpadtool.domain.entity.HandoverCarJudge;
import com.gpad.gpadtool.repository.HandoverCarJudgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName HandoverCarJudgeService.java
 * @Description TODO
 * @createTime 2023年09月22日 17:52:00
 */
@Service
public class HandoverCarJudgeService {
    @Autowired
    private HandoverCarJudgeRepository handoverCarJudgeRepository;
    
    public HandoverCarJudgeDto saveHandoverCarJudgeDto(HandoverCarJudgeDto handoverCarJudgeDto) {
        handoverCarJudgeDto.setId(new SnowIdGenerator().nextId());
        handoverCarJudgeDto.setCreateTime(new Date());
        handoverCarJudgeRepository.save(JSONObject.parseObject(JSONObject.toJSONString(handoverCarJudgeDto), HandoverCarJudge.class));
        return handoverCarJudgeDto;
    }

    
    public HandoverCarJudgeDto getBybussinessNo(String bussinessNo) {
        LambdaQueryWrapper<HandoverCarJudge> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HandoverCarJudge::getBussinessNo,bussinessNo);
        HandoverCarJudge handoverCarJudge = handoverCarJudgeRepository.getOne(wrapper);
        return JSONObject.parseObject(JSONObject.toJSONString(handoverCarJudge),HandoverCarJudgeDto.class);
    }

    
    public void saveHandoverCarJudgeDtoList(List<HandoverCarJudgeDto> list) {
        List<HandoverCarJudge> handoverCarJudges = new ArrayList<>();
        list.forEach(l->{
            l.setId(new SnowIdGenerator().nextId());
            l.setCreateTime(new Date());
            handoverCarJudges.add(JSONObject.parseObject(JSONObject.toJSONString(l),HandoverCarJudge.class));
        });
        handoverCarJudgeRepository.saveBatch(handoverCarJudges);
    }

    
    public void batchSaveOrUpdateHandoverCarJudgeDtoList(List<HandoverCarJudgeDto> list) {
        list.forEach(this::saveOrUpdateHandoverCarJudgeDto);
    }

    
    public void saveOrUpdateHandoverCarJudgeDto(HandoverCarJudgeDto handoverCarJudgeDto) {
        HandoverCarJudgeDto bybussinessNo = this.getBybussinessNo(handoverCarJudgeDto.getBussinessNo());
        if (bybussinessNo==null){
            saveHandoverCarJudgeDto(handoverCarJudgeDto);
        }else {
            updateById(handoverCarJudgeDto);
        }
    }

    
    public HandoverCarJudgeDto updateById(HandoverCarJudgeDto handoverCarJudgeDto){
        handoverCarJudgeRepository.updateById(JSONObject.parseObject(JSONObject.toJSONString(handoverCarJudgeDto),HandoverCarJudge.class));
        return handoverCarJudgeDto;
    }
}
