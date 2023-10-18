package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderReserveVo.java
 * @Description 对接GRT推送预约交车信息入参Dto
 * @createTime 2023年09月22日 14:18:00
 */
@Data
@ApiModel(value = "OrderReserveVo",description = "对接GRT推送预约交车信息入参Dto")
public class OrderReserveVo {

    @ApiModelProperty(value =  "订单号")
    private String businessNo;
    @ApiModelProperty(value =  "订单类型（普通订单，集合订单）")
    private String orderType;
    @ApiModelProperty(value =  "预约人")
    private String customerName;
    @ApiModelProperty(value =  "预约人电话")
    private String mobilePhone;
    @ApiModelProperty(value =  "预约交车时间")
    private String deliveringDate;

    public OrderReserveDto toOrderReserveDto(){
        OrderReserveDto orderReserveDto = new OrderReserveDto();
        orderReserveDto.setBusinessNo(this.businessNo);
        orderReserveDto.setOrderType(this.orderType);
        orderReserveDto.setReservationUser(customerName);
        orderReserveDto.setReservationPhone(this.mobilePhone);
        orderReserveDto.setReservationDay(this.deliveringDate);
        return orderReserveDto;
    }
}
