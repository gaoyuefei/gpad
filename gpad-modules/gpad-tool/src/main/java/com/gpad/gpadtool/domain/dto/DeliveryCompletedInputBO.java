package com.gpad.gpadtool.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
@ApiModel(value = "DeliveryCompletedInputBO",description = "流程节点")
public class DeliveryCompletedInputBO {

    @ApiModelProperty(value = "id")
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String bussinessNo;

    @ApiModelProperty(value = "当前登录账号")
    private String  productUsername;

    @ApiModelProperty(value = "状态")
    private String  status;

    @ApiModelProperty(value = "交车准备页面Bo")
    private HandoverCarPrepareDto  handoverCarPrepareDto;

}
