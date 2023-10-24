package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderReserveDto.java
 * @Description 交车预约信息Dto
 * @createTime 2023年09月21日 15:56:00
 */
@Data
@ApiModel(value = "OrderReserveDto",description = "交车预约信息Dto")
public class OrderReserveDto {
    @ApiModelProperty(value =  "id")
    private String id;
    @ApiModelProperty(value =  "订单号")
    private String bussinessNo;
    @ApiModelProperty(value =  "订单类型（普通订单，集合订单）")
    private String orderType;
    @ApiModelProperty(value =  "预约交车人")
    private String reservationUser;
    @ApiModelProperty(value =  "预约手机号码")
    private String reservationPhone;
    @ApiModelProperty(value =  "预约交车日期")
    private String reservationDay;
    @ApiModelProperty(value =  "备注")
    private String remark;
    @ApiModelProperty(value =  "创建人")
    private String createBy;
    @ApiModelProperty(value =  "更新人")
    private String updateBy;
    private Date createTime;
    private Date updateTime;
}
