package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderNoDto.java
 * @Description GRT待交车订单号Dto
 * @createTime 2023年09月21日 15:54:00
 */
@Data
@ApiModel(value = "OrderNoDto",description = "GRT待交车订单号Dto")
public class OrderNoDto {
    @ApiModelProperty(value =  "id")
    private String id;
    @ApiModelProperty(value =  "订单号")
    private String businessNo;
    @ApiModelProperty(value =  "集合订单ID")
    private String mergeOrderId;
    @ApiModelProperty(value =  "订单状态")
    private String orderStatus;
    @ApiModelProperty(value =  "订单类型")
    private Integer orderType;
    @ApiModelProperty(value =  "店代码")
    private String dealerCode;
    @ApiModelProperty(value =  "是否大客户订单")
    private String isBigCustomer;
    @ApiModelProperty(value =  "车主姓名")
    private String customerName;
    @ApiModelProperty(value =  "手机号")
    private String mobilePhone;
    @ApiModelProperty(value =  "车型名称")
    private String seriesName;
    @ApiModelProperty(value =  "内饰色")
    private String trimColor;
    @ApiModelProperty(value =  "外观色")
    private String color;
    @ApiModelProperty(value =  "销售顾问名称")
    private String consultant;
    @ApiModelProperty(value =  "销售顾问ID")
    private String consultantId;
    @ApiModelProperty(value =  "销售日期")
    private String sheetCreateDate;
    @ApiModelProperty(value =  "预约交车日")
    private String deliveringDate;
    @ApiModelProperty(value =  "交车完成日")
    private String finishDate;
    @ApiModelProperty(value =  "配车时间")
    private String dispatchedDate;
    @ApiModelProperty(value =  "车辆到店时间")
    private String stockinTime;
    private Date createTime;
    private Date updateTime;
}
