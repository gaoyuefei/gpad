package com.gpad.gpadtool.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gpad.common.security.utils.SecurityUtils;
import com.gpad.gpadtool.constant.SnowIdGenerator;
import com.gpad.gpadtool.domain.dto.HandoverCarCheckInfoDto;
import com.gpad.gpadtool.domain.dto.HandoverCarCheckInfoOutBO;
import com.gpad.gpadtool.domain.entity.FileInfo;
import com.gpad.gpadtool.domain.entity.GpadIdentityAuthInfo;
import com.gpad.gpadtool.domain.entity.HandoverCarCheckInfo;
import com.gpad.gpadtool.repository.FileInfoRepository;
import com.gpad.gpadtool.repository.GpadIdentityAuthInfoRepository;
import com.gpad.gpadtool.repository.HandoverCarCheckInfoRepository;
import com.gpad.gpadtool.repository.OrderDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private GpadIdentityAuthInfoRepository gpadIdentityAuthInfoRepository;


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
        log.info("method:queryDeliverCarConfirmInfo().开始执行: {}", JSON.toJSONString(handoverCarCheckInfoDto));
        String username = SecurityUtils.getUsername();
        //账号
        String account = username;
        Integer flag = 0;
        String bussinessNo = handoverCarCheckInfoDto.getBussinessNo();

        HandoverCarCheckInfoOutBO handoverCarCheckInfoOutBO = new HandoverCarCheckInfoOutBO();

        GpadIdentityAuthInfo gpadIdentityAuthInfo = gpadIdentityAuthInfoRepository.checkMemorySign(account);
        log.info("method:checkMemorySign(){}", JSON.toJSONString(gpadIdentityAuthInfo));
        if (ObjectUtil.isNotEmpty(gpadIdentityAuthInfo)){
            if (StringUtils.isNotBlank(gpadIdentityAuthInfo.getAccount())){
                //查询是否实名信息给前端
                flag = 1;
                handoverCarCheckInfoOutBO.setMemorySignPath(gpadIdentityAuthInfo.getFilePath());
                handoverCarCheckInfoOutBO.setAccountId(gpadIdentityAuthInfo.getId()+"");
                log.info("已实名参数为{}", flag);
            }
        }
        handoverCarCheckInfoOutBO.setRealName(flag);
        //查询收据库交车确认信息
        HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoRepository.queryDeliverCarConfirmInfo(bussinessNo);
        BeanUtil.copyProperties(handoverCarCheckInfo,handoverCarCheckInfoOutBO);
        log.info("method:queryDeliverCarConfirmInfo().交车准备内容: {}", JSONObject.toJSONString(handoverCarCheckInfo));

        //查询当前账号可以用签名
        FileInfo fileInfo = fileInfoRepository.checkSignPath(account, handoverCarCheckInfoDto.getLinkType(), handoverCarCheckInfoDto.getFileType());
        if (!ObjectUtil.isEmpty(fileInfo)){
            handoverCarCheckInfoOutBO.setMemorySignPath(fileInfo.getFilePath());
        }
        //记忆签
        if (ObjectUtil.isNotEmpty(handoverCarCheckInfo)){
            handoverCarCheckInfoOutBO.setId(handoverCarCheckInfo.getId());
        }
        handoverCarCheckInfoOutBO.setIsDelivery(handoverCarCheckInfo.getIsDelivery());
        handoverCarCheckInfoOutBO.setId(handoverCarCheckInfo.getId());
        log.info("method:queryDeliverCarConfirmInfo().执行结束: {}", JSON.toJSONString(handoverCarCheckInfoOutBO));

        return handoverCarCheckInfoOutBO;
    }

    public Boolean saveDeliverCarConfirmInfo(HandoverCarCheckInfoDto handoverCarCheckInfoDto) {
        return handoverCarCheckInfoRepository.saveDeliverCarConfirmInfo(handoverCarCheckInfoDto);
    }

    public HandoverCarCheckInfo getListBybussinessNo(String bussinessNo) {
        HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoRepository.queryDeliverCarConfirmInfo(bussinessNo);
        log.info("查询订单返回列表为：{}",JSON.toJSONString(handoverCarCheckInfo));
        return handoverCarCheckInfo;
    }
}
