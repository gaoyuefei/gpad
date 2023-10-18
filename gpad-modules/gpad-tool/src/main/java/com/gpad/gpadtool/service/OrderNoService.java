package com.gpad.gpadtool.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gpad.common.core.constant.StatusCode;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.gpadtool.constant.IsBigCustomer;
import com.gpad.gpadtool.constant.OrderType;
import com.gpad.gpadtool.constant.PageFactory;
import com.gpad.gpadtool.domain.dto.OrderListByPageVo;
import com.gpad.gpadtool.domain.entity.OrderNo;
import com.gpad.gpadtool.domain.dto.OrderNoDto;
import com.gpad.gpadtool.domain.dto.PageResult;
import com.gpad.gpadtool.repository.OrderNoRepository;
import com.gpad.gpadtool.utils.UuidUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderNoService.java
 * @Description TODO
 * @createTime 2023年09月22日 15:18:00
 */
@Service
public class OrderNoService {
    @Autowired
    private OrderNoRepository orderNoRepository;

    public OrderNoDto getByBusinessNo(String businessNo){
        LambdaQueryWrapper<OrderNo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderNo::getBusinessNo,businessNo);
        OrderNo orderNo = orderNoRepository.getOne(wrapper);
        return JSONObject.parseObject(JSONObject.toJSONString(orderNo),OrderNoDto.class);
    }

    public List<OrderNoDto> getByBusinessNos(List<String> businessNos){
        LambdaQueryWrapper<OrderNo> wrapper = Wrappers.lambdaQuery();
        wrapper.in(OrderNo::getBusinessNo,businessNos);
        List<OrderNo> orderNos = orderNoRepository.list(wrapper);
        List<OrderNoDto> result = new ArrayList<>();
        orderNos.forEach(o->result.add(JSONObject.parseObject(JSONObject.toJSONString(o),OrderNoDto.class)));
        return result;
    }

    public void saveOrderNoList(List<OrderNoDto> list){
        List<OrderNo> orderNos = new ArrayList<>();
        list.forEach(l->orderNos.add(JSONObject.parseObject(JSONObject.toJSONString(l),OrderNo.class)));
        orderNoRepository.saveBatch(orderNos);
    }


    public void batchSaveOrUpdateOrderNoList(List<OrderNoDto> list){
        list.forEach(this::saveOrUpdateOrderNo);
    }


    public void saveOrUpdateOrderNo(OrderNoDto orderNoDto){
        OrderNoDto byBusinessNo = this.getByBusinessNo(orderNoDto.getBusinessNo());
        if (byBusinessNo==null){
            saveOrderNoDto(orderNoDto);
        }else {
            updateById(orderNoDto);
        }
    }

    public OrderNoDto updateById(OrderNoDto orderNoDto){
        OrderNoDto byBusinessNo = this.getByBusinessNo(orderNoDto.getBusinessNo());
        orderNoDto.setId(byBusinessNo.getId());
        orderNoDto.setUpdateTime(new Date());
        orderNoRepository.updateById(JSONObject.parseObject(JSONObject.toJSONString(orderNoDto),OrderNo.class));
        return orderNoDto;
    }

    public OrderNoDto saveOrderNoDto(OrderNoDto orderNoDto){
        orderNoDto.setId(UuidUtil.generateUuid());
        orderNoDto.setCreateTime(new Date());
        orderNoRepository.save(JSONObject.parseObject(JSONObject.toJSONString(orderNoDto),OrderNo.class));
        return orderNoDto;
    }


    public List<OrderNoDto> saveOrUpdateOrderSet(List<OrderNoDto> orderNoDtos){
        int size = orderNoDtos.size();
        //检查集合订单里的数据是否为大客户订单
        orderNoDtos.removeIf(o->o.getIsBigCustomer().equals(IsBigCustomer.NO.getInfo()));
        if (size != orderNoDtos.size()){
            throw new ServiceException("集合订单中不能包含非大客户订单!请检查数据后重新操作或者联系管理员!",StatusCode.FAILURE.getValue());
        }
        List<String> businessNos = new ArrayList<>();
        //检查集合订单里的数据是否已存在其他集合中
        orderNoDtos.forEach(o->businessNos.add(o.getBusinessNo()));
        List<OrderNoDto> byBusinessNos = this.getByBusinessNos(businessNos);
        List<String> otherSetNos = new ArrayList<>();
        byBusinessNos.forEach(b->otherSetNos.add(b.getMergeOrderId()));
        if (CollectionUtils.isNotEmpty(otherSetNos)){
            throw new ServiceException("订单【"+JSONObject.toJSONString(otherSetNos)+"】已存在于其他集合订单，请重新选择!",StatusCode.FAILURE.getValue());
        }

        //TODO 已预约的大客户订单是否支持集合？

        //处理集合订单
        orderNoDtos.forEach(o->{
            //生成集合订单ID
            o.setMergeOrderId(UuidUtil.generateUuid());
            //添加是否集合订单标记
            o.setOrderType(OrderType.MERGE_ORDER.getCode());
        });
        this.batchSaveOrUpdateOrderNoList(orderNoDtos);
        return orderNoDtos;
    }

    public List<OrderNoDto> ListOrderByMergeId(String mergeId){
        LambdaQueryWrapper<OrderNo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderNo::getMergeOrderId,mergeId);
        List<OrderNo> list = orderNoRepository.list(wrapper);
        List<OrderNoDto> result = new ArrayList<>();
        list.forEach(l->result.add(JSONObject.parseObject(JSONObject.toJSONString(l),OrderNoDto.class)));
        return result;
    }


    public PageResult<OrderNoDto> orderListByPage(OrderListByPageVo orderListByPageVo){
        IPage<OrderNo> page = PageFactory.createPage(orderListByPageVo);
        page = orderNoRepository.pageOrderList(page,orderListByPageVo);
        List<OrderNo> records = page.getRecords();
        List<OrderNoDto> orderNoDtos = new ArrayList<>();
        records.forEach(r->orderNoDtos.add(JSONObject.parseObject(JSONObject.toJSONString(r),OrderNoDto.class)));
        return new PageResult<>(orderNoDtos,page.getCurrent(),page.getSize(),page.getTotal());
    }


    public List<OrderNoDto> listOrderByParam(OrderListByPageVo orderListByPageVo){
        List<OrderNo> orderNos = orderNoRepository.getOrderListByParam(orderListByPageVo);
        List<OrderNoDto> orderNoDtos = new ArrayList<>();
        orderNos.forEach(r->orderNoDtos.add(JSONObject.parseObject(JSONObject.toJSONString(r),OrderNoDto.class)));
        return orderNoDtos;
    }
}
