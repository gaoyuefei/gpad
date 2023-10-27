package com.gpad.gpadtool.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StartDeliveryCarOutBO",description = "开始交车OutBO")
public class StartDeliveryCarOutBO {

    @ApiModelProperty(value = "订单号")
    private String bussinessNo;

}
