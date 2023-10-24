package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Builder
@Accessors(chain = true)
@TableName("grt_order_detail")
@JsonSerialize
@ApiModel(value = "OrderDetail",description = "GTR订单详情表")
public class OrderDetail {

    @TableId(type = IdType.ASSIGN_ID)
    /**
     * id
     */
    private String id;

    /**
     * 订单号
     */
    private String bussinessNo;

    /**
     * 开票完成时间
     */
    private String invoiceDate;

    /**
     * 开票状态
     */
    private String invoiceStatus;

    /**
     * 发票号
     */
    private String billingNo;

    /**
     * 是否已结清
     */
    private String payOffStatus;

    /**
     * 结清时间
     */
    private String payOffDate;

    /**
     * 预计交车时间
     */
    private String deliveringDate;

    /**
     * 车架号
     */
    private String vin;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 是否大客户订单
     */
    private String isBigCustomer;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 车辆到店时间
     */
    private String stockinTime;

    /**
     * 车主姓名
     */
    private String customerName;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 车系名称
     */
    private String bigSeriesName;

    /**
     * 车型名称
     */
    private String seriesName;

    /**
     * 动力配置名称
     */
    private String modelName;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 内饰色
     */
    private String trimColor;

    /**
     * 外观色
     */
    private String color;

    /**
     * 选装包
     */
    private String oppgName;

    /**
     * 销售顾问
     */
    private String consultant;

    /**
     * 销售日期
     */
    private String sheetCreateDate;

    /**
     * pdi单号
     */
    private String pdiOrderNo;

    /**
     * pdi实施状态
     */
    private String pdiOrderStatus;

    /**
     * pdi完成时间
     */
    private String pdiOrderFinishDate;

    /**
     * 客户交车备注
     */
    private String remark;
    /**
     * 版本号
     */
    private String version;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * update_time
     */
    private Date updateTime;

}
