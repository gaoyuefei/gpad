package com.gpad.gpadtool.repository;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.domain.dto.OrderDetailResultDto;
import com.gpad.gpadtool.domain.entity.OrderDetail;
import com.gpad.gpadtool.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderDetailRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 16:47:00
 */
@Service
public class OrderDetailRepository extends ServiceImpl<OrderDetailMapper, OrderDetail>  {

    public OrderDetail getPadOrderDetail(String bussinessNo) {
        List<OrderDetail> list = this.lambdaQuery().eq(OrderDetail::getBussinessNo, bussinessNo).orderByDesc(OrderDetail::getCreateTime).list();
        return CollectionUtils.isEmpty(list)? (null):(list.get(0));
    }

    public Boolean saveOrderDetailEntity(OrderDetailResultDto orderDetailResultDto,String bussinessNo) {

        OrderDetail orderDetail = OrderDetail.builder()
                .bussinessNo(bussinessNo)
                .invoiceDate(orderDetailResultDto.getInvoiceDate())
                .invoiceStatus(orderDetailResultDto.getInvoiceStatus())
                .billingNo(orderDetailResultDto.getBillingNo())
                // 支付状态 是否已结清
                .payOffStatus(orderDetailResultDto.getPayOffStatus())
                .payOffDate(orderDetailResultDto.getPayOffDate())
                .deliveringDate(orderDetailResultDto.getDeliveringDate())
                .vin(orderDetailResultDto.getVin())
                .orderType(orderDetailResultDto.getOrderType())
                .bigSeriesName(orderDetailResultDto.getBigSeriesName())
                .orderStatus(orderDetailResultDto.getOrderStatus())
                .mobilePhone(orderDetailResultDto.getMobilePhone())
                .seriesName(orderDetailResultDto.getSeriesName())
                .productName(orderDetailResultDto.getProductName())
                .trimColor(orderDetailResultDto.getTrimColor())
                .color(orderDetailResultDto.getColor())
                .consultant(orderDetailResultDto.getConsultant())
                .sheetCreateDate(orderDetailResultDto.getSheetCreateDate())
                .remark(orderDetailResultDto.getRemark())
                .createTime(new Date())
                .build();
        return this.save(orderDetail);
    }

    public OrderDetail queryOrderDetail(String bussinessNo) {
        OrderDetail orderDetail = OrderDetail.builder().build();
        List<OrderDetail> list = this.lambdaQuery().eq(OrderDetail::getBussinessNo, bussinessNo).list();
        if (CollectionUtil.isEmpty(list) && list.size() > 0 ){
            orderDetail = list.get(0);
        }
        return orderDetail;
    }

    public Boolean saveOrUpdateNextRemark(String bussinessNo, String handoverCarRemark) {
        return this.lambdaUpdate()
                .setSql(" version = version + 1 ")
                .set(OrderDetail::getRemark,handoverCarRemark)
                .set(OrderDetail::getUpdateTime,new Date())
                .eq(OrderDetail::getBussinessNo,bussinessNo)
//                .eq(OrderDetail::getId,flow.getId())
                .update();

    }

}
