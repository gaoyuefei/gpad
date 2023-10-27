package com.gpad.gpadtool.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gpad.common.core.domain.R;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.gpadtool.constant.FlowNodeNum;
import com.gpad.gpadtool.domain.dto.FileInfoDto;
import com.gpad.gpadtool.domain.dto.FlowInfoDto;
import com.gpad.gpadtool.domain.dto.HandoverCarPrepareOutBO;
import com.gpad.gpadtool.domain.entity.FileInfo;
import com.gpad.gpadtool.domain.entity.HandoverCarPrepare;
import com.gpad.gpadtool.domain.dto.HandoverCarPrepareDto;
import com.gpad.gpadtool.domain.entity.OrderDetail;
import com.gpad.gpadtool.repository.FileInfoRepository;
import com.gpad.gpadtool.repository.FlowInfoRepository;
import com.gpad.gpadtool.repository.HandoverCarPrepareRepository;
import com.gpad.gpadtool.repository.OrderDetailRepository;
import com.gpad.gpadtool.utils.RedisLockUtils;
import com.gpad.gpadtool.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
        Boolean result = false;
        String bussinessNo = handoverCarPrepareDto.getBussinessNo();

            try {
                RedisLockUtils.lock(bussinessNo);
                if (Strings.isBlank(handoverCarPrepareDto.getId()) && !checkedbussinessNo(bussinessNo)) {
                    //幂等处理
                    System.out.println("锁");
                    //交车准备信息入库  bussinessNo -> 绑定的文件服务器
                    result = handoverCarPrepareRepository.saveReadyDeliverCarInfoOrderNo(handoverCarPrepareDto);
                    if (!result) {
                        throw new ServiceException("交车准备信息入库", R.FAIL);
                    }
                    result = fileInfoRepository.saveReadyDeliverCarFile(handoverCarPrepareDto.getLinkType());
                    if (!result) {
                        throw new ServiceException("bussinessNo -> 绑定的文件服务器", R.FAIL);
                    }

                    log.info("method:saveReadyDeliverCarInfoOrderNo().交车准备内容: {}", "result");
                }else {
                    //TODO
                    updateById(handoverCarPrepareDto);
                    result = true;
//                    handoverCarPrepareRepository.updateReadyDeliverCarInfoOrderNo(handoverCarPrepareDto);
                }
                FlowInfoDto bybussinessNo = flowInfoRepository.getBybussinessNo(handoverCarPrepareDto.getBussinessNo());
                Integer nodeNum = bybussinessNo.getNodeNum();

                if (nodeNum == 2 && "1".equals(handoverCarPrepareDto.getButton())){
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
                System.out.println("释放锁");
                RedisLockUtils.unlock(bussinessNo);
            }
        return R.ok(result);
    }

    private boolean checkedbussinessNo(String bussinessNo) {
        return handoverCarPrepareRepository.checkedbussinessNo(bussinessNo);
    }


    public HandoverCarPrepareDto updateById(HandoverCarPrepareDto handoverCarPrepareDto){
        boolean result = handoverCarPrepareRepository.updateById(JSONObject.parseObject(JSONObject.toJSONString(handoverCarPrepareDto), HandoverCarPrepare.class));
        if (!result){
            throw new ServiceException("交车准备页面更新失败",R.FAIL);
        }
        return handoverCarPrepareDto;
    }

    public HandoverCarPrepareOutBO queryReadyDeliverCarOrderNo(HandoverCarPrepareDto handoverCarPrepareDto) {

        HandoverCarPrepareOutBO readyDeliverCarOutBO = new HandoverCarPrepareOutBO();

        String bussinessNo = handoverCarPrepareDto.getBussinessNo();
        if (StringUtils.isBlank(bussinessNo)){
            throw new ServiceException("订单编号有误",R.FAIL);
        }

//        //查询订单详情
//        OrderDetail orderDetail = orderDetailRepository.queryOrderDetail(bussinessNo);
//        if (ObjectUtil.isEmpty(orderDetail)){
//            throw new ServiceException("请在确认车辆到店,订单状态有误",R.FAIL);
//        }

        HandoverCarPrepare handoverCarPrepare = handoverCarPrepareRepository.queryBybussinessNo(bussinessNo);
        readyDeliverCarOutBO.setBussinessNo(bussinessNo);
        BeanUtil.copyProperties(handoverCarPrepare,readyDeliverCarOutBO);

        String supplies = readyDeliverCarOutBO.getSupplies();
        if(!org.apache.commons.lang3.StringUtils.isBlank(supplies)){
            String substring = supplies.substring(1, supplies.length() - 1);
            List<Integer> list = new ArrayList<>();
            String[] split = substring.split(",");
            for (String s : split) {
                list.add(Integer.valueOf(s));
            }
            readyDeliverCarOutBO.setSuppliesAtt(list);
        }
        readyDeliverCarOutBO.setId(handoverCarPrepare.getId());

        List<FileInfo> fileInfos = fileInfoRepository.queryFileBybussinessNo(bussinessNo);
        List<FileInfoDto> list = new ArrayList<>();
        FileInfoDto fileInfoDto = new FileInfoDto();
        fileInfos.forEach(fileInfo -> {
            BeanUtils.copyProperties(fileInfo,fileInfoDto);
            list.add(fileInfoDto);
        });
        readyDeliverCarOutBO.setLinkType(list);
        return readyDeliverCarOutBO;
    }
}
