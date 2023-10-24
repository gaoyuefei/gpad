package com.gpad.gpadtool.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gpad.gpadtool.domain.entity.OrderReserve;
import com.gpad.gpadtool.domain.dto.OrderReserveDto;
import com.gpad.gpadtool.repository.OrderReserveRepository;
import com.gpad.gpadtool.utils.UuidUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderReserveService.java
 * @Description TODO
 * @createTime 2023年09月22日 17:03:00
 */
@Service
public class OrderReserveService {
    @Autowired
    private OrderReserveRepository orderReserveRepository;

    public OrderReserveDto getBybussinessNo(String bussinessNo){
        LambdaQueryWrapper<OrderReserve> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderReserve::getBussinessNo,bussinessNo);
        OrderReserve orderReserve = orderReserveRepository.getOne(wrapper);
        return JSONObject.parseObject(JSONObject.toJSONString(orderReserve),OrderReserveDto.class);
    }

    public OrderReserveDto saveOrderReserveDto(OrderReserveDto orderReserveDto) {
        orderReserveDto.setId(UuidUtil.generateUuid());
        orderReserveRepository.save(JSONObject.parseObject(JSONObject.toJSONString(orderReserveDto), OrderReserve.class));
        return orderReserveDto;
    }

    public void saveOrderReserveDtoList(List<OrderReserveDto> list) {
        List<OrderReserve> orderReserves = new ArrayList<>();
        list.forEach(l->orderReserves.add(JSONObject.parseObject(JSONObject.toJSONString(l),OrderReserve.class)));
        orderReserveRepository.saveBatch(orderReserves);
    }

    public void batchSaveOrUpdateOrderReserveDtoList(List<OrderReserveDto> list) {
        list.forEach(this::saveOrUpdateOrderReserveDto);
    }

    public void saveOrUpdateOrderReserveDto(OrderReserveDto orderReserveDto) {
        if (Strings.isBlank(orderReserveDto.getId()) || this.getBybussinessNo(orderReserveDto.getBussinessNo()) == null){
            saveOrderReserveDto(orderReserveDto);
        }
        updateById(orderReserveDto);
    }

    public OrderReserveDto updateById(OrderReserveDto orderReserveDto){
        orderReserveRepository.updateById(JSONObject.parseObject(JSONObject.toJSONString(orderReserveDto),OrderReserve.class));
        return orderReserveDto;
    }
}
