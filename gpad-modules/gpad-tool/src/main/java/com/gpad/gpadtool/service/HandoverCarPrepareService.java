package com.gpad.gpadtool.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.gpadtool.constant.CommCode;
import com.gpad.gpadtool.constant.FlowNodeNum;
import com.gpad.gpadtool.domain.dto.*;
import com.gpad.gpadtool.domain.entity.FileInfo;
import com.gpad.gpadtool.domain.entity.HandoverCarCheckInfo;
import com.gpad.gpadtool.domain.entity.HandoverCarPrepare;
import com.gpad.gpadtool.enums.PayMethodToCodeEnum;
import com.gpad.gpadtool.repository.FileInfoRepository;
import com.gpad.gpadtool.repository.FlowInfoRepository;
import com.gpad.gpadtool.repository.HandoverCarPrepareRepository;
import com.gpad.gpadtool.repository.OrderDetailRepository;
import com.gpad.gpadtool.utils.RedisLockUtils;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
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

    @Autowired
    private HandoverCarCheckInfoService handoverCarCheckInfoService;

    @Autowired
    private GRTService grtService;


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

    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> saveOrUpdateHandoverCarPrepareDto(HandoverCarPrepareDto handoverCarPrepareDto) {
        log.info("开始执行方法 --->>> method:saveOrUpdateHandoverCarPrepareDto{}", JSONObject.toJSONString(handoverCarPrepareDto));
        Boolean result = false;
        Integer nodeNum = 0;
        String bussinessNo = handoverCarPrepareDto.getBussinessNo();

        //查库补ID
        HandoverCarPrepare handoverCarPrepare = handoverCarPrepareRepository.queryBybussinessNo(bussinessNo);
        handoverCarPrepareDto.setId(StringUtils.isNotEmpty(handoverCarPrepare.getId())?handoverCarPrepare.getId():null);
        log.info("查库补ID 结果为{}", JSONObject.toJSONString(handoverCarPrepare));

        try {
                RedisLockUtils.lock(bussinessNo);

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

                if(4 == nodeNum){
                    throw new ServiceException("UI非法操作", R.FAIL);
                }

                if (2 >= nodeNum  && "1".equals(handoverCarPrepareDto.getButton())){
                    //扭转流程订单流程状态
                    FlowInfoDto flow = new FlowInfoDto();
                    flow.setBussinessNo(handoverCarPrepareDto.getBussinessNo());
                    flow.setNodeNum(FlowNodeNum.HAND_OVER_CAR_CONFIRM.getCode());
                    flow.setUpdateTime(new Date());
                    result = flowInfoRepository.updateDeliverCarReadyToConfirm(flow);
                    if (!result){
                        throw new ServiceException("流程状态不合法", R.FAIL);
                    }
                }
            } finally {
                RedisLockUtils.unlock(bussinessNo);
            }
        return R.ok(result);
    }

    public void batchSaveOrUpdateHandoverCarPrepareDtoList(List<HandoverCarPrepareDto> list) {
        list.forEach(this::saveOrUpdateHandoverCarPrepareDto);
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
        String bussinessNo = handoverCarPrepareDto.getBussinessNo();
        HandoverCarPrepareOutBO readyDeliverCarOutBO = new HandoverCarPrepareOutBO();

        R<List<OrderDetailResultDto>> grtOrderDetail = grtService.getGrtOrderDetail(handoverCarPrepareDto.getBussinessNo());
        List<OrderDetailResultDto> data = grtOrderDetail.getData();
        log.info("GRT详情返回数据{}", JSON.toJSONString(data));
        if (CollectionUtil.isEmpty(data)){
            throw new ServiceException("交车准备页面GRT订单详情数据网络开小差，请联系管理员并重试",CommCode.DATA_NOT_FOUND.getCode());
        }
        log.info("打印订单详情数据-》》》{}",JSON.toJSONString(data));
        HandoverCarPrepare handoverCarPrepare = handoverCarPrepareRepository.queryBybussinessNo(bussinessNo);

        readyDeliverCarOutBO.setBussinessNo(bussinessNo);
        BeanUtil.copyProperties(handoverCarPrepare,readyDeliverCarOutBO);

        OrderDetailResultDto orderDetailResultDto = data.get(0);
        //读取订单详情状态
        //GRT返回的属性赋值给返回前端的Bo
        readyDeliverCarOutBO.setPaymentMethod(PayMethodToCodeEnum.getPadValueByType(orderDetailResultDto.getPaymentMethod()));
        readyDeliverCarOutBO.setInvoiceStatus(grtChineseToEnumValue(orderDetailResultDto.getInvoiceStatus()));
        readyDeliverCarOutBO.setPayOffStatus(grtChineseToEnumValue(orderDetailResultDto.getPayOffStatus()));
        readyDeliverCarOutBO = getStatusByDbOrGRT(handoverCarPrepare,readyDeliverCarOutBO);

        FlowInfoDto bybussinessNo = flowInfoRepository.getBybussinessNo(handoverCarPrepareDto.getBussinessNo());
        Integer nodeNum = bybussinessNo.getNodeNum();
        log.info("当前节点信息为 --->>>:bussinessNoBo{}，当前状态{}", JSON.toJSONString(bybussinessNo),nodeNum);
        if (null == nodeNum){
            nodeNum = 0;
        }

        if (nodeNum >= 3){
            String id = handoverCarPrepare.getId();
            if (StringUtils.isNotEmpty(id)){
                readyDeliverCarOutBO.setLoanStatus(handoverCarPrepare.getLoanStatus());
                readyDeliverCarOutBO.setUnifiedSalesInvoice(handoverCarPrepare.getUnifiedSalesInvoice());
                //TODO 新增的四个字段，到这一步骤，直接取数据库，不取GRT
                log.info("进入开始交车，强制不能更改-》》》{}",nodeNum);
            }
        }

        readyDeliverCarOutBO.setId(handoverCarPrepare.getId());
        List<FileInfo> fileInfos = fileInfoRepository.queryFileBybussinessNo(bussinessNo);
        List<FileInfoDto> list = new ArrayList<>();
        fileInfos.forEach(fileInfo -> {
            FileInfoDto fileInfoDto = new FileInfoDto();
            BeanUtils.copyProperties(fileInfo,fileInfoDto);
            list.add(fileInfoDto);
        });
        readyDeliverCarOutBO.setLinkType(list);

        HandoverCarCheckInfo handoverCarCheckInfo = handoverCarCheckInfoService.getListBybussinessNo(bussinessNo);
        log.info("查询返回实体为: {}", JSONObject.toJSONString(handoverCarCheckInfo));

        Integer isDelivery = handoverCarCheckInfo.getIsDelivery();
        readyDeliverCarOutBO.setIsDelivery(null == isDelivery ?0:isDelivery);
        log.info("打印实体状态: {}", JSONObject.toJSONString(isDelivery));

        log.info("交车准备页面OUTBo数据-》》》{}",JSON.toJSONString(readyDeliverCarOutBO));
        return readyDeliverCarOutBO;
    }

    public HandoverCarPrepareOutBO getStatusByDbOrGRT(HandoverCarPrepare handoverCarPrepare, HandoverCarPrepareOutBO readyDeliverCarOutBO) {
        String supplies = handoverCarPrepare.getSupplies();
        readyDeliverCarOutBO.setSupplies(supplies);

        //TODO  GRT返回的值赋值给返回前端的值

        //状态转换
        if (null == handoverCarPrepare.getLoanStatus()){
            readyDeliverCarOutBO.setLoanStatus(0);
        }
        if (null == handoverCarPrepare.getUnifiedSalesInvoice()){
            readyDeliverCarOutBO.setUnifiedSalesInvoice(0);
        }

        if ("0".equals(readyDeliverCarOutBO.getInvoiceStatus())){
            readyDeliverCarOutBO.setUnifiedSalesInvoice(1);
        }

        if ("0".equals(readyDeliverCarOutBO.getPayOffStatus())){
            readyDeliverCarOutBO.setLoanStatus(1);
        }

        return readyDeliverCarOutBO;
    }

    public String grtChineseToEnumValue(String invoiceStatus) {
        if ("是".equals(invoiceStatus)){
            return "0";
        }
        if ("否".equals(invoiceStatus)){
            return "1";
        }
        return "1";
    }


    public HandoverCarPrepareDto selectByBussinessNo(String bussinessNo) {
        return handoverCarPrepareRepository.selectByBussinessNo(bussinessNo);
    }
}
