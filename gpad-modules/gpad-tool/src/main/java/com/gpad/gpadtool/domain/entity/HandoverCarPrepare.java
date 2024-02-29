package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("handover_car_prepare")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HandoverCarPrepare implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     *  订单号
     **/
    private String bussinessNo;

    /**
     *  尾款是否支付（0：未支付，1：已支付）
     **/
    private Integer loanStatus;

    /**
     *  销售统一发票
     **/
    private Integer unifiedSalesInvoice;

    /**
     *  发票图片链接
     **/
    private String invoiceImgUrl;

    /**
     *  交强险投保单号
     **/
    private String compulsoryInsuranceNo;

    /**
     *  交强险保险公司
     **/
    private String compulsoryInsuranceCompanyId;

    /**
     *  商业险投保单号
     **/
    private String commercialInsuranceNo;

    /**
     *  商保保险公司
     **/
    private String commercialInsuranceCompanyId;

    /**
     *  是否购置税
     **/
    private Integer purchaseTax;

    /**
     *  车辆合格证
     **/
    private Integer carCertificate;

    /**
     *  车辆保险
     **/
    private Integer carInsure;

    /**
     *  用品及随车附件
     **/
    private String supplies;

    /**
     *  车辆牌照(0：蓝牌,1:绿牌,2=临牌)
     **/
    private Integer carLicense;

    /**
     *  车牌号
     **/
    private String licensePlateNum;

    /**
     *  交车准备项备注
     **/
    private String remark;

    private Date createTime;

    private Date updateTime;
}
