package com.gpad.gpadtool.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.domain.entity.OrderReserve;
import com.gpad.gpadtool.mapper.OrderReserveMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderReserveRepositoryjava
 * @Description TODO
 * @createTime 2023年09月22日 17:02:00
 */
@Slf4j
@Service
public class OrderReserveRepository extends ServiceImpl<OrderReserveMapper, OrderReserve> {
}
