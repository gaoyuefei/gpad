package com.gpad.gpadtool.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "HandoverCarPrepareDto",description = "交车准备Dto")
public class HandoverCarDto {

    @ApiModelProperty(value = "订单号")
    private String bussinessNo;
    @ApiModelProperty(value = "订单类型（1=普通订单、2=集合订单）")
    private Integer orderType;
    @ApiModelProperty(value = "交车流程信息Dto")
    private FlowInfoDto flowInfoDto;
    @ApiModelProperty(value = "交车准备Dto")
    private HandoverCarPrepareDto handoverCarPrepareDto;
    @ApiModelProperty(value = "交车确认Dto")
    private HandoverCarCheckInfoDto handoverCarCheckInfoDto;
    @ApiModelProperty(value = "交车前检查是否完成--1=是;0=否")
    private Integer checkBeforeHandoverCar;
    @ApiModelProperty(value = "车辆到店--交车需求备注")
    private String handoverCarRemark;

    @ApiModelProperty(value = "文件列表")
    private List<FileInfoDto> files;

}
