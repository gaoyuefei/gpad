package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("handover_car_prepare")
public class HandoverCarPrepare implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String id;
    /**
     *  订单号
     **/
    private String businessNo;

    /**
     *  放款状态
     **/
    private Integer loanStatus;

    /**
     *  销售统一发票
     **/
    private Integer unifiedSalesInvoice;

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
    private Integer supplies;

    /**
     *  车辆牌照(0=未办理,1=正式牌,2=临牌)
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
