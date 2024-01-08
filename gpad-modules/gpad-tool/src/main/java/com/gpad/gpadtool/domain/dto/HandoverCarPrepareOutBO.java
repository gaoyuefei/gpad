package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author LF
 * @version 1.0.0
 * @ClassName HandoverCarPrepareDto.java
 * @Description 交车准备Dto
 * @createTime 2023年09月22日 17:20:00
 */
@Data
@ApiModel(value = "HandoverCarPrepareOutBO",description = "交车准备BO")
public class HandoverCarPrepareOutBO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "订单号")
    private String bussinessNo;

    @ApiModelProperty(value = "PAD放款状态()")
    private Integer loanStatus;

    @ApiModelProperty(value = "支付方式：0：默认：1一次性付清，2：分期付款 ，3：其他：4:无")
    private String paymentMethod;

    @ApiModelProperty(value = "PAD 销售统一发票")
    private Integer unifiedSalesInvoice;

    @ApiModelProperty(value = "GRT 是否已结清")
    private String payOffStatus;

    @ApiModelProperty(value = " GRT   开票状态")
    private String invoiceStatus;

    @ApiModelProperty(value = "车辆保险")
    private Integer carInsure;

    @ApiModelProperty(value = "车辆牌照(0=未办理,1=正式牌,2=绿牌,3=临牌)")
    private Integer carLicense;

    @ApiModelProperty(value = "车辆合格证")
    private Integer carCertificate;

    @ApiModelProperty(value = "车牌号")
    private String licensePlateNum;

    @ApiModelProperty(value = "交车准备项备注")
    private String remark;

    @ApiModelProperty(value = "交车准备项备注")
    private Integer purchaseTax;

    @ApiModelProperty(value = "用品及随车附件")
    private String supplies;

    @ApiModelProperty(value = "用品及随车附件")
    private List<Integer> suppliesAtt;

    @ApiModelProperty(value = "车况检查及交车仪式")
    private List<FileInfoDto> linkType;

    @ApiModelProperty(value = "0：未完成交车，1：已完成交车")
    private Integer isDelivery;

    private Date createTime;

    private Date updateTime;
    
}
