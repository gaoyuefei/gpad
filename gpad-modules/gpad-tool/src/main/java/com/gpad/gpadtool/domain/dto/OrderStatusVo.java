package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderStatusVo.java
 * @Description 对接GRT推送订单状态变更入参Dto
 * @createTime 2023年09月22日 14:16:00
 */
@Data
@ApiModel(value = "OrderStatusVo",description = "对接GRT推送订单状态变更入参Dto")
public class OrderStatusVo {
    @ApiModelProperty(value =  "订单号")
    private String bussinessNo;
    @ApiModelProperty(value =  "车架号")
    private String vin;
    @ApiModelProperty(value =  "车牌号")
    private String license;
    @ApiModelProperty(value =  "是否交车标识")
    private String isDelivery;
}
