package com.gpad.gpadtool.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gpad.gpadtool.domain.dto.OrderListByPageVo;
import com.gpad.gpadtool.domain.entity.OrderNo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderNoMapper.java
 * @Description TODO
 * @createTime 2023年09月22日 15:57:00
 */
@Mapper
public interface OrderNoMapper extends BaseMapper<OrderNo> {
    IPage<OrderNo> pageOrderList(IPage<OrderNo> page, OrderListByPageVo orderListByPageVo);

    List<OrderNo> getOrderListByParam(OrderListByPageVo orderListByPageVo);
}
