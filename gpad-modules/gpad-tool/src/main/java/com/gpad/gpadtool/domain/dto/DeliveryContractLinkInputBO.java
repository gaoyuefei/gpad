package com.gpad.gpadtool.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
@ApiModel(value = "DeliveryContractLinkInputBO",description = "交车合同连接")
public class DeliveryContractLinkInputBO {

    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String bussinessNo;

}
