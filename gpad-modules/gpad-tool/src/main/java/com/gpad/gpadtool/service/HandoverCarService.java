package com.gpad.gpadtool.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.gpadtool.constant.CommCode;
import com.gpad.gpadtool.constant.FlowNodeNum;
import com.gpad.gpadtool.domain.dto.*;
import com.gpad.gpadtool.domain.entity.FileInfo;
import com.gpad.gpadtool.domain.entity.HandoverCarCheckInfo;
import com.gpad.gpadtool.repository.*;
import com.gpad.gpadtool.utils.RedisLockUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
    private HandoverCarCheckInfoRepository handoverCarCheckInfoRepository;
    @Autowired
    private GRTService grtService;
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

    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> HandOverCarNext(HandoverCarOutBO handoverCarOutBO) {
        Boolean result = false;
        String bussinessNo = handoverCarOutBO.getBussinessNo();
        try {
            //幂等处理
            RedisLockUtils.lock(bussinessNo);
            //变更订单 remak 订单实施状态
             result = orderDetailRepository.saveOrUpdateNextRemark(handoverCarOutBO.getBussinessNo(), handoverCarOutBO.getHandoverCarRemark());
             if(!result){
                 throw new ServiceException("修改备注信息失败，请重新保存",500);
             }
            //变更交车流程信息  handover_car_flow_info 修改流程状态
            FlowInfoDto bybussinessNo = flowInfoRepository.getBybussinessNo(handoverCarOutBO.getBussinessNo());
            Integer nodeNum = bybussinessNo.getNodeNum();
            if (null == nodeNum ){
                nodeNum = 0;
            }
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
            HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoService.getListBybussinessNo(bussinessNo);
            log.info("查询返回实体为: {}", JSONObject.toJSONString(handoverCarCheckInfo));
            Long handoverCarCheckInfoId = handoverCarCheckInfo.getId();
            log.info("查询返回实体为ID: {}", handoverCarCheckInfoId);
            handoverCarCheckInfoDto.setId(handoverCarCheckInfoId);
            //交车确认信息入库
            result = handoverCarCheckInfoService.saveDeliverCarConfirmInfo(handoverCarCheckInfoDto);
            if (!result){
                throw new ServiceException("合同信息保存失败",CommCode.DATA_UPDATE_WRONG.getCode());
            }
            log.info("method:saveDeliverCarConfirmInfo().合同信息成功: {}", JSONObject.toJSONString(handoverCarCheckInfoDto));
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

    public R<FileInfoOutBo> getDeliveryCeremonyPath(DeliveryCeremonyInputBO deliveryCeremonyInputBO) {
        FileInfoOutBo fileInfoOutBo = new FileInfoOutBo();
        List<FileInfoDto> list = new ArrayList<>();
        R<List<OrderDetailResultDto>> grtOrderDetail = grtService.getGrtOrderDetail(deliveryCeremonyInputBO.getBussinessNo());
        log.info("method:getGrtOrderDetail().详情数据为: {}", JSONObject.toJSONString(grtOrderDetail));
        List<OrderDetailResultDto> data = grtOrderDetail.getData();
        if (CollectionUtils.isNotEmpty(data)){
            if (data.size()>0){
                OrderDetailResultDto orderDetailResultDto = data.get(0);
                fileInfoOutBo.setCustomerName(orderDetailResultDto.getCustomerName());
                fileInfoOutBo.setSuitCarType(orderDetailResultDto.getSeriesName());
            }
        }
        List<FileInfo> fileInfos = fileInfoRepository.getDeliveryCeremonyPath(deliveryCeremonyInputBO.getBussinessNo(), deliveryCeremonyInputBO.getFileType(),"22");
        if(CollectionUtils.isNotEmpty(fileInfos)){
            if (fileInfos.size() > 0){
                log.info("method:getDeliveryCeremonyPath().文件数据为: {}", JSONObject.toJSONString(grtOrderDetail));
                fileInfos.forEach(fileInfo -> {
                    FileInfoDto fileInfoDto = new FileInfoDto();
                    BeanUtil.copyProperties(fileInfo,fileInfoDto);
                    list.add(fileInfoDto);
                    fileInfoDto = null;
                });
                fileInfoOutBo.setFileInfoDto(list);
                log.info("method:getDeliveryCeremonyPath().交车仪式数据为: {}", JSONObject.toJSONString(fileInfoOutBo));
            }
        }
        return R.ok(fileInfoOutBo);
    }

    public R<FileInfoOutBo> getHandOverCarH5File(FileInfoInputBO fileInfoInputBO) {
        FileInfoOutBo fileInfoOutBo = new FileInfoOutBo();
        List<FileInfoDto> list = new ArrayList<>();
        R<List<OrderDetailResultDto>> grtOrderDetail = grtService.getGrtOrderDetail(fileInfoInputBO.getBussinessNo());
        log.info("method:getGrtOrderDetail().详情数据为: {}", JSONObject.toJSONString(grtOrderDetail));
        List<OrderDetailResultDto> data = grtOrderDetail.getData();
        if (data.size()>0){
            OrderDetailResultDto orderDetailResultDto = data.get(0);
            fileInfoOutBo.setCustomerName(orderDetailResultDto.getCustomerName());
            fileInfoOutBo.setSuitCarType(orderDetailResultDto.getSeriesName());
        }
        List<FileInfo> fileInfos = fileInfoRepository.getDeliveryCeremonyPath(fileInfoInputBO.getBussinessNo(), fileInfoInputBO.getFileType(),fileInfoInputBO.getLinkType());
        log.info("method:getDeliveryCeremonyPath().文件数据为: {}", JSONObject.toJSONString(grtOrderDetail));
        fileInfos.forEach(fileInfo -> {
            FileInfoDto fileInfoDto = new FileInfoDto();
            BeanUtil.copyProperties(fileInfo,fileInfoDto);
            list.add(fileInfoDto);
            //标记GC
            fileInfoDto = null;
        });
        fileInfoOutBo.setFileInfoDto(list);
        log.info("method:getDeliveryCeremonyPath().交车仪式数据为: {}", JSONObject.toJSONString(fileInfoOutBo));
        return R.ok(fileInfoOutBo);
    }

    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> deliveryCompletedNext(DeliveryCompletedInputBO deliveryCompletedInputBO) {
        Boolean result = false;
        String status = "";
        String message = "";
        String bussinessNo = deliveryCompletedInputBO.getBussinessNo();
        try {
            //幂等处理
            RedisLockUtils.lock(bussinessNo);

            FlowInfoDto bybussinessNo = flowInfoRepository.getBybussinessNo(bussinessNo);
            if (ObjectUtil.isNotEmpty(bybussinessNo)) {
                Integer nodeNum = bybussinessNo.getNodeNum();
                if ("2".equals(nodeNum+"")){
                    return R.ok(null,200,"检测交车准备页面或者交车确认页面存在未完成项，请完成后重试");
                }
                if("3".equals(nodeNum+"")){
                    FlowInfoDto flowInfoDto = new FlowInfoDto();
                    flowInfoDto.setBussinessNo(bussinessNo);
                    flowInfoDto.setNodeNum(FlowNodeNum.HAND_OVER_CAR_GUIDE.getCode());
                    result = flowInfoRepository.updateDeliverCarReadyToConfirm(flowInfoDto);
                    if (!result){
                        throw new ServiceException("当前流程状态更新有误,请刷新后重试", CommCode.DATA_UPDATE_WRONG.getCode());
                    }

                    R<List<OrderDetailResultDto>> grtOrderDetail = grtService.getGrtOrderDetail(bussinessNo);
                    List<OrderDetailResultDto> data = grtOrderDetail.getData();
                    if (CollectionUtil.isEmpty(data)){
                       throw new ServiceException("交车完成查询VIN获取失败,请重试",CommCode.DATA_IS_WRONG.getCode());
                    }
                    OrderDetailResultDto orderDetailResultDto = data.get(0);
                    HandoverCarPrepareDto handoverCarPrepareDto = handoverCarPrepareService.selectByBussinessNo(bussinessNo);
                    log.info("交车确认页面handoverCarPrepareDto{}",JSON.toJSONString(handoverCarPrepareDto));
                    if(ObjectUtil.isEmpty(handoverCarPrepareDto)){
                        throw new ServiceException("交车完成,获取车牌失败，请重试",CommCode.DATA_IS_WRONG.getCode());
                    }

                    HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoRepository.queryDeliverCarConfirmInfo(bussinessNo);
                    log.info("交车确认页面handoverCarCheckInfo{}",JSON.toJSONString(handoverCarPrepareDto));
                    if (ObjectUtil.isEmpty(handoverCarCheckInfo.getBussinessNo())){
                        throw new ServiceException("检测有未完成项,请先完成线上或线下签署。",CommCode.DATA_IS_WRONG.getCode());
                    }

                    if (null == handoverCarCheckInfo.getId() && 9999 <= handoverCarCheckInfo.getId()){
                        throw new ServiceException("检测有未完成项,请先完成线上或线下签署。",CommCode.DATA_IS_WRONG.getCode());
                    }

                    if (StringUtils.isEmpty(handoverCarCheckInfo.getContractAplNo())){
                        if (!("1".equals(handoverCarCheckInfo.getSignType()+"") && "1".equals(handoverCarCheckInfo.getSignStatus()+""))){
                            throw new ServiceException("检测有未完成项,请先完成线上或线下签署。",CommCode.DATA_IS_WRONG.getCode());
                        }
                    }else {
                        if ("0".equals(handoverCarCheckInfo.getSignType()+"") && "0".equals(handoverCarCheckInfo.getSignStatus()+"")){
                            throw new ServiceException("检测有未完成项,请先完成线上或线下签署。",CommCode.DATA_IS_WRONG.getCode());
                        }
                    }

                    handoverCarCheckInfo.setIsDelivery(1);
                    boolean b = handoverCarCheckInfoRepository.updateById(handoverCarCheckInfo);
                    if (!b){
                        throw new ServiceException("交车信息不一致，请重新核对信息后提交",CommCode.DATA_IS_WRONG.getCode());
                    }
                    OrderStatusVo orderStatusVo = new OrderStatusVo();
                    orderStatusVo.setLicense(handoverCarPrepareDto.getLicensePlateNum());
                    orderStatusVo.setVin(orderDetailResultDto.getVin());
                    orderStatusVo.setBussinessNo(bussinessNo);
                    orderStatusVo.setIsDelivery("1");
                    log.info("打印调用GRT状态变更入参{}",JSON.toJSONString(orderStatusVo));
                    R r = grtService.changeOrderStatus2Grt(orderStatusVo);
                    String dataOut = JSON.toJSONString(r);

                    if (StringUtils.isNotEmpty(dataOut)){
                        log.info("交车完成参数为{}",JSON.toJSONString(dataOut));
                        int code = r.getCode();
                        String msg = r.getMsg();
                        if ("200".equals(code+"")){
                           return R.ok(true,msg);
                        }else {
                            throw new ServiceException(msg ,CommCode.DATA_UPDATE_WRONG.getCode());
                        }
//                        throw new ServiceException(message,500);
                    }else {
                       throw new ServiceException("调用外部接口网络异常，请重试",500);
                    }
                }
                return R.ok(null,200,"当前流程节点已更新,请求重新加载页面");
            }
            return R.fail(null,CommCode.PARAM_IS_BLANK.getCode(),CommCode.PARAM_IS_BLANK.getMessage());
        } finally {
            RedisLockUtils.unlock(bussinessNo);
        }
    }

    public R<String> getContractLinkByBussinessNo(DeliveryContractLinkInputBO deliveryContractLinkInputBO) {
        String bussinessNo = deliveryContractLinkInputBO.getBussinessNo();
        HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoService.getListBybussinessNo(bussinessNo);
        log.info("查询合同连接为{}",JSON.toJSONString(handoverCarCheckInfo));
        return R.ok(handoverCarCheckInfo.getContractLink());
    }

    public R deliveryEvaluationNext(String bussinessNo) {
        Boolean result = false;
        Integer nodeNum = 0;
        FlowInfoDto bybussinessNo = flowInfoRepository.getBybussinessNo(bussinessNo);
        log.info("当前节点信息为 --->>>:bybussinessNo{}", JSON.toJSONString(bybussinessNo));

        if (ObjectUtil.isNotEmpty(bybussinessNo)){
            nodeNum = bybussinessNo.getNodeNum();
        }

        if(null == nodeNum){
            nodeNum = 0;
        }

        if (4 == nodeNum){
            FlowInfoDto flowInfoDto = new FlowInfoDto();
            flowInfoDto.setBussinessNo(bussinessNo);
            flowInfoDto.setNodeNum(FlowNodeNum.HAND_OVER_CAR_JUDGE.getCode());
            result = flowInfoRepository.updateDeliverCarReadyToConfirm(flowInfoDto);
            if (!result){
                throw new ServiceException("当前流程状态更新有误,请刷新后重试", CommCode.DATA_UPDATE_WRONG.getCode());
            }
            log.info("交车评价下一步信息为{}",JSON.toJSONString(flowInfoDto));
        }
        return R.ok(result);
    }
}
