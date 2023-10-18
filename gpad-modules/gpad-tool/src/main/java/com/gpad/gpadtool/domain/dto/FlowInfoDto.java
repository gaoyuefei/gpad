package com.gpad.gpadtool.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "FlowInfoDto",description = "交车流程Dto")
public class FlowInfoDto {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "订单号")
    private String businessNo;
    @ApiModelProperty(value = "订单类型（普通订单、集合订单）")
    private Integer orderType;
    @ApiModelProperty(value = "流程节点（1车辆到店，2交车准备，3交车确认，4用车指南，5交车评价）")
    private Integer nodeNum;
    @ApiModelProperty(value = "创建人")
    private String createBy;
    @ApiModelProperty(value = "修改人")
    private String updateBy;
    private Date createTime;
    private Date updateTime;

}
