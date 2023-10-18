package com.gpad.gpadtool.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.domain.dto.OrderListByPageVo;
import com.gpad.gpadtool.domain.entity.OrderNo;
import com.gpad.gpadtool.mapper.OrderNoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderNoRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 15:59:00
 */
@Slf4j
@Service
public class OrderNoRepository extends ServiceImpl<OrderNoMapper, OrderNo> {

    public IPage<OrderNo> pageOrderList(IPage<OrderNo> page, OrderListByPageVo orderListByPageVo){
        return this.baseMapper.pageOrderList(page,orderListByPageVo);
    }

    public List<OrderNo> getOrderListByParam(OrderListByPageVo orderListByPageVo){
        return this.baseMapper.getOrderListByParam(orderListByPageVo);
    }
}
