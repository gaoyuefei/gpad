package com.gpad.gpadtool.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gpad.gpadtool.domain.entity.OrderDetail;
import com.gpad.gpadtool.domain.dto.OrderDetailDto;
import com.gpad.gpadtool.repository.OrderDetailRepository;
import com.gpad.gpadtool.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderDetailService.java
 * @Description TODO
 * @createTime 2023年09月22日 16:44:00
 */
@Service
public class OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public OrderDetailDto getByBusinessNo(String businessNo) {
        LambdaQueryWrapper<OrderDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderDetail::getBusinessNo,businessNo);
        OrderDetail OrderDetail = orderDetailRepository.getOne(wrapper);
        return JSONObject.parseObject(JSONObject.toJSONString(OrderDetail),OrderDetailDto.class);
    }

    public void saveOrderDetailList(List<OrderDetailDto> list) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        list.forEach(l->orderDetails.add(JSONObject.parseObject(JSONObject.toJSONString(l),OrderDetail.class)));
        orderDetailRepository.saveBatch(orderDetails);
    }

    public void batchSaveOrUpdateOrderDetailList(List<OrderDetailDto> list) {
        list.forEach(this::saveOrUpdateOrderDetail);
    }

    public void saveOrUpdateOrderDetail(OrderDetailDto orderDetailDto){
        OrderDetailDto byBusinessNo = this.getByBusinessNo(orderDetailDto.getBusinessNo());
        if (byBusinessNo==null){
            saveOrderDetailDto(orderDetailDto);
        }else {
            updateById(orderDetailDto);
        }
    }

    public OrderDetailDto updateById(OrderDetailDto orderDetailDto){
        OrderDetailDto byBusinessNo = this.getByBusinessNo(orderDetailDto.getBusinessNo());
        orderDetailDto.setId(byBusinessNo.getId());
        orderDetailDto.setUpdateTime(new Date());
        orderDetailRepository.updateById(JSONObject.parseObject(JSONObject.toJSONString(orderDetailDto), OrderDetail.class));
        return orderDetailDto;
    }

    public OrderDetailDto saveOrderDetailDto(OrderDetailDto orderDetailDto){
        orderDetailDto.setId(UuidUtil.generateUuid());
        orderDetailDto.setCreateTime(new Date());
        orderDetailRepository.save(JSONObject.parseObject(JSONObject.toJSONString(orderDetailDto),OrderDetail.class));
        return orderDetailDto;
    }

    public OrderDetailDto getOrderDetailByBusinessNo(String businessNo){
        LambdaQueryWrapper<OrderDetail> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderDetail::getBigSeriesName,businessNo);
        return null;
    }
}
