package com.gpad.gpadtool.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.gpadtool.constant.FlowNodeNum;
import com.gpad.gpadtool.domain.dto.*;
import com.gpad.gpadtool.repository.FileInfoRepository;
import com.gpad.gpadtool.repository.FlowInfoRepository;
import com.gpad.gpadtool.repository.OrderDetailRepository;
import com.gpad.gpadtool.utils.RedisLockUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName HandoverCarService.java
 * @Description TODO
 * @createTime 2023年09月22日 18:00:00
 */
@Service
@Slf4j
public class HandoverCarService {
    @Autowired
    private FlowInfoRepository flowInfoRepository;
    @Autowired
    private FileInfoRepository fileInfoRepository;
    @Autowired
    private HandoverCarPrepareService handoverCarPrepareService;
    @Autowired
    private HandoverCarCheckInfoService handoverCarCheckInfoService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;



    public R<Void> HandOverCar(HandoverCarDto handoverCarDto) {
        HandoverCarPrepareDto handoverCarPrepareDto = handoverCarDto.getHandoverCarPrepareDto();
        HandoverCarCheckInfoDto handoverCarCheckInfoDto = handoverCarDto.getHandoverCarCheckInfoDto();
        FlowInfoDto flowInfoDto = handoverCarDto.getFlowInfoDto();

        FlowInfoDto localFlowInfo = flowInfoRepository.getBybussinessNo(handoverCarDto.getBussinessNo());
        if (localFlowInfo != null){
            Integer nodeNum = localFlowInfo.getNodeNum();
            Integer nowNode = flowInfoDto.getNodeNum();
            if (FlowNodeNum.HAND_OVER_CAR_GUIDE.getCode().equals(nowNode) || nodeNum > nowNode){
                //如果当前节点为用车指南不做操作直接下一步
                //如果当前节点比数据库已存在的流程节点小，说明用户查看已处理的节点后点击下一步，不做操作直接返回
                return R.ok();
            }
        }

        //1、保存图片信息
        if (CollectionUtils.isNotEmpty(handoverCarDto.getFiles())){
            fileInfoRepository.batchSaveOrUpdateFileInfoDtoList(handoverCarDto.getFiles());
        }

        if (flowInfoDto == null){
            //交车第一步
            Integer checkBeforeHandoverCar = handoverCarDto.getCheckBeforeHandoverCar();
            if (checkBeforeHandoverCar == null){
                return R.fail("交车前检查是否完成不能为空! ");
            }
            //车辆到店--交车需求备注
            String handoverCarRemark = handoverCarDto.getHandoverCarRemark();
            //TODO 保存车辆到店备注和交车前检查

            //新建交车流程信息并入库 -- 步骤为第一步
            FlowInfoDto flow = new FlowInfoDto();
            flow.setBussinessNo(handoverCarDto.getBussinessNo());
            flow.setOrderType(handoverCarDto.getOrderType());
            flow.setNodeNum(FlowNodeNum.ARRIVE_STORE.getCode());
            flowInfoRepository.saveFlowInfoDto(flow);
        }else {
            //非第一步
            if (handoverCarPrepareDto != null){
                //交车准备流程  入库交车准备信息
                handoverCarPrepareService.saveOrUpdateHandoverCarPrepareDto(handoverCarPrepareDto);
            }else if (handoverCarCheckInfoDto !=null){
                //交车确认流程  入库交车确认信息
                handoverCarCheckInfoService.saveOrUpdateHandoverCarCheckInfoDto(handoverCarCheckInfoDto);
            }else {
                //都为空，返回错误信息
                return R.fail("请检查参数! ");
            }
            //4、更新交车流程节点信息
            flowInfoDto.setNodeNum(Objects.equals(flowInfoDto.getNodeNum(), FlowNodeNum.HAND_OVER_CAR_JUDGE.getCode()) ?
                    FlowNodeNum.HAND_OVER_CAR_JUDGE.getCode():flowInfoDto.getNodeNum()+1);
            flowInfoRepository.updateById(flowInfoDto);
        }
        return R.ok();
    }

    public R<Boolean> HandOverCarNext(HandoverCarOutBO handoverCarOutBO) {
        Boolean result = false;
        String bussinessNo = handoverCarOutBO.getBussinessNo();
        try {
            //幂等处理
            RedisLockUtils.lock(bussinessNo);
            //变更订单 remak 订单实施状态
            orderDetailRepository.saveOrUpdateNextRemark(handoverCarOutBO.getBussinessNo(),handoverCarOutBO.getHandoverCarRemark());
            //变更交车流程信息  handover_car_flow_info 修改流程状态
            FlowInfoDto bybussinessNo = flowInfoRepository.getBybussinessNo(handoverCarOutBO.getBussinessNo());
            Integer nodeNum = bybussinessNo.getNodeNum();

            if ( nodeNum < 2){
                FlowInfoDto flow = new FlowInfoDto();
                flow.setBussinessNo(handoverCarOutBO.getBussinessNo());
                flow.setNodeNum(FlowNodeNum.HAND_OVER_CAR_PREPARE.getCode());
                result = flowInfoRepository.updateDeliverCarReadyToConfirm(flow);
                if (!result){
                    throw new ServiceException("流程节点变化有误",500);
                }
            }
        } finally {
            RedisLockUtils.unlock(bussinessNo);
        }

        return R.ok(true);
    }

    public R<Boolean> saveDeliverCarConfirmInfo(HandoverCarCheckInfoDto handoverCarCheckInfoDto) {
        Boolean result = false;
        String bussinessNo = handoverCarCheckInfoDto.getBussinessNo();
        try {
            //幂等处理
            RedisLockUtils.lock(bussinessNo);
            //交车确认信息入库
            result = handoverCarCheckInfoService.saveDeliverCarConfirmInfo(handoverCarCheckInfoDto);
            if (!result){
                throw new ServiceException("合同信息入库失败",500);
            }
            log.info("method:saveDeliverCarConfirmInfo().合同信息入库失败: {}", JSONObject.toJSONString(handoverCarCheckInfoDto));
           //TODO  保存记忆签图片

            //
            //新建交车流程信息并入库 -- 步骤为第一步 //
//            FlowInfoDto flowInfoDto = new FlowInfoDto();
//            flowInfoDto.setBussinessNo(bussinessNo);
//            flowInfoDto.setNodeNum(FlowNodeNum.ARRIVE_STORE.getCode());
//            flowInfoDto.setVersion(0);
//            result = flowInfoRepository.saveFlowInfoFirstNode(flowInfoDto);
//            if (!result){
//                throw new ServiceException("流程接口入库失败",500);
//            }
        } finally {
            RedisLockUtils.unlock(bussinessNo);
        }
        return R.ok(true);
    }

    public FlowInfoDto queryProcessNode(String bussinessNo) {
        FlowInfoDto flowInfoDto = flowInfoRepository.getBybussinessNo(bussinessNo);
        return ObjectUtil.isEmpty(flowInfoDto)?(new FlowInfoDto()):(flowInfoDto);
    }
}
