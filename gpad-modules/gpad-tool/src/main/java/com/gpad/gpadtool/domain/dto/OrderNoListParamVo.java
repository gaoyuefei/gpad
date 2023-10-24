package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderNoListParamVo.java
 * @Description 对接GRT查询订单列表入参Dto
 * @createTime 2023年09月22日 11:30:00
 */
@Data
@ApiModel(value = "OrderNoListParamVo",description = "对接GRT查询订单列表入参Dto")
public class OrderNoListParamVo {
    @NotBlank
    @ApiModelProperty(value = "登录账号")
    private String userCode;

    @ApiModelProperty(value = "店代码")
    private String dealerCode;

    @ApiModelProperty(value = "车主姓名")
    private String customerName;

    @ApiModelProperty(value = "车主手机号码")
    private String mobilePhone;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    @ApiModelProperty(value = "预计交车开始时间")
    private String deliveryTimeS;

    @ApiModelProperty(value = "预计交车结束时间")
    private String deliveryTimeE;


    @ApiModelProperty(value = "预计交车开始时间")
    private String reservationHandoverDateStart;

    @ApiModelProperty(value = "预计交车结束时间")
    private String reservationHandoverDateEnd;

    @ApiModelProperty(value = "交车完成日")
    private String finishHandoverDateStart;

    @ApiModelProperty(value = "交车完成结束日")
    private String  finishHandoverDateEnd;

    @ApiModelProperty(value = "是否超时")
    private String isOverTime;

    @ApiModelProperty(value = "销售日期开始时间")
    private String sheetCreateDateStart;

    @ApiModelProperty(value = "销售日期结束时间")
    private String sheetCreateDateEnd;

    @NotBlank
    @ApiModelProperty(value =  "当前页码")
    private Integer pageNum;
    @NotBlank
    @ApiModelProperty(value =  "总条数")
    private Integer pageSize;

    @ApiModelProperty(value = "订单更新开始时间   yyyy-MM-dd hh24:mi:ss")
    private String updatedStart;

    @ApiModelProperty(value = "订单更新结束时间 yyyy-MM-dd hh24:mi:ss")
    private String updatedEnd;

}
