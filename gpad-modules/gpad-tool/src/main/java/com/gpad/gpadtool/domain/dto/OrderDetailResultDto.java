package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderDetailResultDto.java
 * @Description TODO
 * @createTime 2023年09月22日 11:46:00
 */
@Data
@ApiModel(value = "OrderDetailResultDto", description = "GRT待交车订单详情Dto")
public class OrderDetailResultDto {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "订单号")
    private String bussinessNo;
    @ApiModelProperty(value = "开票完成时间")
    private String invoiceDate;
    @ApiModelProperty(value = "开票状态")
    private String invoiceStatus;
    @ApiModelProperty(value = "发票号")
    private String billingNo;
    @ApiModelProperty(value = "发票文件列表")
    private List<String> billFiles;
    @ApiModelProperty(value = "是否已结清")
    private String payOffStatus;
    @ApiModelProperty(value = "结清时间")
    private String payOffDate;
    @ApiModelProperty(value = "预计交车时间")
    private String deliveringDate;
    @ApiModelProperty(value = "车架号")
    private String vin;
    @ApiModelProperty(value = "订单类型")
    private String orderType;
    @ApiModelProperty(value = "是否大客户订单")
    private String isBigCustomer;
    @ApiModelProperty(value = "订单状态")
    private String orderStatus;
    @ApiModelProperty(value = "支付方式")
    private String paymentMethod;
    @ApiModelProperty(value = "车辆到店时间")
    private String stockinTime;
    @ApiModelProperty(value = "车主姓名")
    private String customerName;
    @ApiModelProperty(value = "手机号")
    private String mobilePhone;
    @ApiModelProperty(value = "车系名称")
    private String bigSeriesName;
    @ApiModelProperty(value = "车型名称")
    private String seriesName;
    @ApiModelProperty(value = "动力配置名称")
    private String modelName;
    @ApiModelProperty(value = "动力配置名称")
    private String modelCode;
    @ApiModelProperty(value = "产品名称")
    private String productName;
    @ApiModelProperty(value = "内饰色")
    private String trimColor;
    @ApiModelProperty(value = "外观色")
    private String color;
    @ApiModelProperty(value = "选装包")
    private String oppgName;
    @ApiModelProperty(value = "销售顾问")
    private String consultant;

    @ApiModelProperty(value = "APP订单号")
    private String ORIGIN_ORDER_NO;

    @ApiModelProperty(value = "销售日期")
    private String sheetCreateDate;
    @ApiModelProperty(value = "PDI单号")
    private String pdiOrderNo;
    @ApiModelProperty(value = "PDI实施状态")
    private String pdiOrderStatus;
    @ApiModelProperty(value = "PDI完成时间")
    private String pdiOrderFinishDate;
    @ApiModelProperty(value = "客户备注信息")
    private String remark;
    //grt开发回复--这个可以不用管   SO_NO_ID
    private Date createTime;
    private Date updateTime;
}
