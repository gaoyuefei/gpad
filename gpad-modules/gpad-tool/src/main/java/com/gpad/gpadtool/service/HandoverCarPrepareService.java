package com.gpad.gpadtool.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.gpadtool.constant.CommCode;
import com.gpad.gpadtool.constant.FlowNodeNum;
import com.gpad.gpadtool.domain.dto.FileInfoDto;
import com.gpad.gpadtool.domain.dto.FlowInfoDto;
import com.gpad.gpadtool.domain.dto.HandoverCarPrepareDto;
import com.gpad.gpadtool.domain.dto.HandoverCarPrepareOutBO;
import com.gpad.gpadtool.domain.entity.FileInfo;
import com.gpad.gpadtool.domain.entity.HandoverCarPrepare;
import com.gpad.gpadtool.repository.FileInfoRepository;
import com.gpad.gpadtool.repository.FlowInfoRepository;
import com.gpad.gpadtool.repository.HandoverCarPrepareRepository;
import com.gpad.gpadtool.repository.OrderDetailRepository;
import com.gpad.gpadtool.utils.RedisLockUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName HandoverCarPrepareService.java
 * @Description TODO
 * @createTime 2023年09月22日 17:18:00
 */
@Service
@Slf4j
public class HandoverCarPrepareService {

    @Autowired
    private HandoverCarPrepareRepository handoverCarPrepareRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private FlowInfoRepository flowInfoRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public HandoverCarPrepareDto getBybussinessNo(String bussinessNo){

        LambdaQueryWrapper<HandoverCarPrepare> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HandoverCarPrepare::getBussinessNo,bussinessNo);
        HandoverCarPrepare handoverCarPrepare = handoverCarPrepareRepository.getOne(wrapper);

        List<FileInfoDto> linkType = fileInfoRepository.getBybussinessNoAndLinkType(bussinessNo, 1);
        //定义路径对象 进行转换返回 TODO
        HandoverCarPrepareDto handoverCarPrepareDto = JSONObject.parseObject(JSONObject.toJSONString(handoverCarPrepare), HandoverCarPrepareDto.class);
//        handoverCarPrepareDto.setLinkType(linkType);
        return handoverCarPrepareDto;
    }

    public HandoverCarPrepareDto saveHandoverCarPrepareDto(HandoverCarPrepareDto handoverCarPrepareDto) {
        boolean result = handoverCarPrepareRepository.save(JSONObject.parseObject(JSONObject.toJSONString(handoverCarPrepareDto), HandoverCarPrepare.class));
        if (!result){
            throw new ServiceException("交车准备页面保存失败",R.FAIL);
        }
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

    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> saveOrUpdateHandoverCarPrepareDto(HandoverCarPrepareDto handoverCarPrepareDto) {
        log.info("开始执行方法 --->>> method:saveOrUpdateHandoverCarPrepareDto{}", JSONObject.toJSONString(handoverCarPrepareDto));
        Boolean result = false;
        Integer nodeNum = 0;
        String bussinessNo = handoverCarPrepareDto.getBussinessNo();
            try {
                RedisLockUtils.lock(bussinessNo);
                //TODO 前端有ID 一定要传ID
                if (Strings.isBlank(handoverCarPrepareDto.getId()) && !checkedbussinessNo(bussinessNo)) {
                    log.info("进入保存方法 --->>> method:saveOrUpdateHandoverCarPrepareDto{},{}", bussinessNo,handoverCarPrepareDto.getId());
                    //幂等处理
                    //交车准备信息入库  bussinessNo -> 绑定的文件服务器
                    result = handoverCarPrepareRepository.saveReadyDeliverCarInfoOrderNo(handoverCarPrepareDto);
                    if (!result) {
                        throw new ServiceException("bussinessNo -> 交车准备信息入库失败", CommCode.DATA_UPDATE_WRONG.getCode());
                    }
                    result = fileInfoRepository.saveReadyDeliverCarFile(handoverCarPrepareDto.getLinkType());
                    if (!result) {
                        throw new ServiceException("bussinessNo -> 绑定的文件服务器", R.FAIL);
                    }
                    log.info("method:saveReadyDeliverCarInfoOrderNo().交车准备内容: {}", result);
                }else {
                    result = updateById(handoverCarPrepareDto);
                    if (!result) {
                        throw new ServiceException("bussinessNo -> 更新文件服务异常", CommCode.DATA_UPDATE_WRONG.getCode());
                    }
                    result = fileInfoRepository.updateReadyDeliverCarFile(handoverCarPrepareDto.getLinkType(),bussinessNo);
                    //TODO 文件服务器异常
                    if (!result) {
                        throw new ServiceException("bussinessNo -> 更新文件服务异常", R.FAIL);
                    }
                }
                FlowInfoDto bybussinessNo = flowInfoRepository.getBybussinessNo(handoverCarPrepareDto.getBussinessNo());
                log.info("当前节点信息为 --->>>:bybussinessNo{}", JSON.toJSONString(bybussinessNo));

                if (ObjectUtil.isNotEmpty(bybussinessNo)){
                    nodeNum = bybussinessNo.getNodeNum();
                }

                if(null == nodeNum){
                    nodeNum = 0;
                }
                if (2 == nodeNum  && "1".equals(handoverCarPrepareDto.getButton())){
                    //扭转流程订单流程状态
                    // TODO 这里还要写 流程节点-扭转流程分离 解耦 目前前是强绑
                    FlowInfoDto flow = new FlowInfoDto();
                    flow.setBussinessNo(handoverCarPrepareDto.getBussinessNo());
                    flow.setNodeNum(FlowNodeNum.HAND_OVER_CAR_CONFIRM.getCode());
                    result = flowInfoRepository.updateDeliverCarReadyToConfirm(flow);
                    if (!result){
                        throw new ServiceException("流程节点分离", R.FAIL);
                    }
                }
            } finally {
                RedisLockUtils.unlock(bussinessNo);
            }
        return R.ok(result);
    }

    private boolean checkedbussinessNo(String bussinessNo) {
        return handoverCarPrepareRepository.checkedbussinessNo(bussinessNo);
    }


    public Boolean updateById(HandoverCarPrepareDto handoverCarPrepareDto){
        log.info("method:updateById().交车准备内容: {}", JSON.toJSONString(handoverCarPrepareDto));
        HandoverCarPrepare handoverCarPrepare = JSONObject.parseObject(JSONObject.toJSONString(handoverCarPrepareDto), HandoverCarPrepare.class);
        handoverCarPrepare.setUpdateTime(new Date());
        String str = handoverCarPrepare.getSupplies();
        if ("[null]".equals(str) || com.gpad.common.core.utils.StringUtils.isEmpty(handoverCarPrepareDto.getSupplies()) || "null".equals(handoverCarPrepareDto.getSupplies())  ){
            handoverCarPrepare.setSupplies("[0]");
        }
        log.info("method:JSONObject.parseObject().转换后{}", JSON.toJSONString(handoverCarPrepareDto));
        boolean result = handoverCarPrepareRepository.updateById(handoverCarPrepare);
        if (!result){
            throw new ServiceException(CommCode.DATA_UPDATE_WRONG.getMessage(),CommCode.DATA_UPDATE_WRONG.getCode());
        }
        return result;
    }

    public HandoverCarPrepareOutBO queryReadyDeliverCarOrderNo(HandoverCarPrepareDto handoverCarPrepareDto) {

        HandoverCarPrepareOutBO readyDeliverCarOutBO = new HandoverCarPrepareOutBO();

        String bussinessNo = handoverCarPrepareDto.getBussinessNo();
        if (StringUtils.isBlank(bussinessNo)){
            throw new ServiceException(CommCode.DATA_IS_WRONG.getMessage(), CommCode.DATA_IS_WRONG.getCode());
        }

        HandoverCarPrepare handoverCarPrepare = handoverCarPrepareRepository.queryBybussinessNo(bussinessNo);
        readyDeliverCarOutBO.setBussinessNo(bussinessNo);
        BeanUtil.copyProperties(handoverCarPrepare,readyDeliverCarOutBO);
        String supplies = handoverCarPrepare.getSupplies();
        readyDeliverCarOutBO.setSupplies(supplies);
        //兼容传值
//        if (null != supplies && !"".equals(supplies) && !"null".equals(supplies)){
//            if(!org.apache.commons.lang3.StringUtils.isBlank(supplies)){
//                String substring = supplies.substring(supplies.indexOf("[")+1, supplies.length()-1);
//                if (!"null".equals(substring)){
//                    List<Integer> list = new ArrayList<>();
//                    if (!org.apache.commons.lang3.StringUtils.isBlank(substring)){
//                        System.out.println(substring);
//                        String[] split = substring.split(",");
//                        for (String s : split) {
//                            list.add(Integer.valueOf(s.trim()));
//                        }
//                        readyDeliverCarOutBO.setSuppliesAtt(list);
//                    }
//                    readyDeliverCarOutBO.setSuppliesAtt(list);
//                }
//            }
//
//        }
        readyDeliverCarOutBO.setId(handoverCarPrepare.getId());
        List<FileInfo> fileInfos = fileInfoRepository.queryFileBybussinessNo(bussinessNo);
        List<FileInfoDto> list = new ArrayList<>();
        fileInfos.forEach(fileInfo -> {
            FileInfoDto fileInfoDto = new FileInfoDto();
            BeanUtils.copyProperties(fileInfo,fileInfoDto);
            list.add(fileInfoDto);
        });
        readyDeliverCarOutBO.setLinkType(list);
        return readyDeliverCarOutBO;
    }

    public HandoverCarPrepareDto selectByBussinessNo(String bussinessNo) {
        return handoverCarPrepareRepository.selectByBussinessNo(bussinessNo);
    }
}
