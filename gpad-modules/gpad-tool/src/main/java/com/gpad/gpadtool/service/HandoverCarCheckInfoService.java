package com.gpad.gpadtool.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gpad.gpadtool.constant.SnowIdGenerator;
import com.gpad.gpadtool.domain.dto.HandoverCarCheckInfoDto;
import com.gpad.gpadtool.domain.dto.HandoverCarCheckInfoOutBO;
import com.gpad.gpadtool.domain.entity.FileInfo;
import com.gpad.gpadtool.domain.entity.HandoverCarCheckInfo;
import com.gpad.gpadtool.domain.entity.OrderDetail;
import com.gpad.gpadtool.repository.FileInfoRepository;
import com.gpad.gpadtool.repository.HandoverCarCheckInfoRepository;
import com.gpad.gpadtool.repository.OrderDetailRepository;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class HandoverCarCheckInfoService {

    @Autowired
    private HandoverCarCheckInfoRepository handoverCarCheckInfoRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    public HandoverCarCheckInfoDto getBybussinessNo(String bussinessNo){
        LambdaQueryWrapper<HandoverCarCheckInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HandoverCarCheckInfo::getBussinessNo,bussinessNo);
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
        if (handoverCarCheckInfoDto.getId() == null || this.getBybussinessNo(handoverCarCheckInfoDto.getBussinessNo()) == null){
            saveHandoverCarCheckInfoDto(handoverCarCheckInfoDto);
        }
        updateById(handoverCarCheckInfoDto);
    }


    public HandoverCarCheckInfoDto updateById(HandoverCarCheckInfoDto handoverCarCheckInfoDto){
        handoverCarCheckInfoRepository.updateById(JSONObject.parseObject(JSONObject.toJSONString(handoverCarCheckInfoDto),HandoverCarCheckInfo.class));
        return handoverCarCheckInfoDto;
    }

    public HandoverCarCheckInfoOutBO queryDeliverCarConfirmInfo(HandoverCarCheckInfoDto handoverCarCheckInfoDto) {

        String bussinessNo = handoverCarCheckInfoDto.getBussinessNo();

        HandoverCarCheckInfoOutBO handoverCarCheckInfoOutBO = new HandoverCarCheckInfoOutBO();
        handoverCarCheckInfoOutBO.setRealName(1);
        //查询收据库交车确认信息
        HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoRepository.queryDeliverCarConfirmInfo(bussinessNo);
        // FIXME  修复每个编号的枚举值
        BeanUtil.copyProperties(handoverCarCheckInfo,handoverCarCheckInfoOutBO);
        log.info("method:queryDeliverCarConfirmInfo().交车准备内容: {}", JSONObject.toJSONString(handoverCarCheckInfo));
        //查询当前账号可以用签名
        FileInfo fileInfo = fileInfoRepository.checkSignPath(bussinessNo, handoverCarCheckInfoDto.getLinkType(), handoverCarCheckInfoDto.getFileType());
        if (!ObjectUtil.isEmpty(fileInfo)){
            handoverCarCheckInfoOutBO.setMemorySignPath(fileInfo.getFilePath());
        }
        // FIXME  新增账号关联表 记忆图片直接返回
        return handoverCarCheckInfoOutBO;
    }

    public Boolean saveDeliverCarConfirmInfo(HandoverCarCheckInfoDto handoverCarCheckInfoDto) {
        return handoverCarCheckInfoRepository.saveDeliverCarConfirmInfo(handoverCarCheckInfoDto);
    }
}
