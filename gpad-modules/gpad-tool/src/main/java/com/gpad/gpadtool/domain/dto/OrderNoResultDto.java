package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderNoResultDto.java
 * @Description TODO
 * @createTime 2023年09月22日 11:28:00
 */
@Data
@ApiModel(value = "OrderNoResultDto",description = "GRT待交车订单号Dto")
public class OrderNoResultDto {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "订单号")
    private String bussinessNo;
    @ApiModelProperty(value = "集合订单ID")
    private String mergeOrderId;
    @ApiModelProperty(value = "订单状态")
    private String orderStatus;
    @ApiModelProperty(value = "订单类型(创新营销订单（E9等）、线下订单、其他)")
    private String orderType;
    @ApiModelProperty(value = "店代码")
    private String dealerCode;
    @ApiModelProperty(value = "是否大客户订单")
    private String isBigCustomer;
    @ApiModelProperty(value = "车主姓名")
    private String customerName;
    @ApiModelProperty(value = "手机号")
    private String mobilePhone;
    @ApiModelProperty(value = "车型名称")
    private String seriesName;
    @ApiModelProperty(value = "内饰色")
    private String trimColor;
    @ApiModelProperty(value = "外观色")
    private String color;
    @ApiModelProperty(value = "销售顾问名称")
    private String consultant;
    @ApiModelProperty(value = "销售顾问ID")
    private String consultantId;
    @ApiModelProperty(value = "销售日期")
    private String sheetCreateDate;
    @ApiModelProperty(value = "预约交车日")
    private String deliveringDate;
    @ApiModelProperty(value = "交车完成日")
    private String finishDate;
    @ApiModelProperty(value = "配车时间")
    private String dispatchedDate;
    @ApiModelProperty(value = "车辆到店时间")
    private String stockinTime;
    @ApiModelProperty(value = "VIN")
    private String vin;
    @ApiModelProperty(value = "绑定状态")
    private String bindStatus;
    private Date createTime;
    private Date updateTime;

    //2023/09/28
    @ApiModelProperty(value = "车主手机号码")
    private String customerPhone;

}
