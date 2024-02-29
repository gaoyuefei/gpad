package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName HandoverCarPrepareDto.java
 * @Description 交车准备Dto
 * @createTime 2023年09月22日 17:20:00
 */
@Data
@ApiModel(value = "HandoverCarPrepareDto",description = "交车准备Dto")
public class HandoverCarPrepareDto {

    @ApiModelProperty(value = "id")
    private String id;

    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String bussinessNo;

    @ApiModelProperty(value = "放款状态()0:未放款，1:已办理")
    private Integer loanStatus;

    @ApiModelProperty(value = "销售统一发票 0:未开具，1:已开具")
    private Integer unifiedSalesInvoice;

    @ApiModelProperty(value = "发票图片链接")
    private String invoiceImgUrl;

    @ApiModelProperty(value = "交强险投保单号")
    private String compulsoryInsuranceNo;

    @ApiModelProperty(value = "交强险保险公司")
    private String compulsoryInsuranceCompanyId;

    @ApiModelProperty(value = "商业险投保单号")
    private String commercialInsuranceNo;

    @ApiModelProperty(value = "商保保险公司")
    private String commercialInsuranceCompanyId;

    @ApiModelProperty(value = "购置税办理 0:未办理，1:已办理")
    private Integer purchaseTax;

    @ApiModelProperty(value = "车辆保险0:未购买，1已购买")
    private Integer carInsure;

    @ApiModelProperty(value = "车辆牌照(0=默认,1=正式牌,2=绿牌,3=临牌)")
    private Integer carLicense;

    @ApiModelProperty(value = "车辆合格证")
    private Integer carCertificate;

    @ApiModelProperty(value = "车牌号")
    private String licensePlateNum;

    @ApiModelProperty(value = "交车准备项备注")
    private String remark;

    @ApiModelProperty(value = "用品及随车附件(0:默认值 1:脚垫 2:地板升级 3:迎宾毯 4:车内窗帘5:迎宾踏板6:车漆保护膜)")
    private List<Integer> supplies;

    @ApiModelProperty(value = "车况检查及交车仪式")
    private List<FileInfoDto> linkType;

    @ApiModelProperty(value = "0:保存，1：开始交车")
    private String button;

    private Date createTime;

    private Date updateTime;
    
}
