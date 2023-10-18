package com.gpad.gpadtool.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.domain.entity.OrderDetail;
import com.gpad.gpadtool.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderDetailRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 16:47:00
 */
@Service
public class OrderDetailRepository extends ServiceImpl<OrderDetailMapper, OrderDetail>  {
}
