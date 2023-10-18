package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

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
    @ApiModelProperty(value = "订单号")
    private String businessNo;
    @ApiModelProperty(value = "放款状态")
    private Integer loanStatus;
    @ApiModelProperty(value = "销售统一发票")
    private Integer unifiedSalesInvoice;
    @ApiModelProperty(value = "车辆合格证")
    private Integer carCertificate;
    @ApiModelProperty(value = "车辆保险")
    private Integer carInsure;
    @ApiModelProperty(value = "用品及随车附件")
    private Integer supplies;
    @ApiModelProperty(value = "车辆牌照(0=未办理,1=正式牌,2=临牌)")
    private Integer carLicense;
    @ApiModelProperty(value = "车牌号")
    private String licensePlateNum;
    @ApiModelProperty(value = "交车准备项备注")
    private String remark;
    private Date createTime;
    private Date updateTime;
    
}
