package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderNo.java
 * @Description TODO
 * @createTime 2023年09月22日 15:55:00
 */
@Data
@Accessors(chain = true)
@TableName("grt_order_no")
@JsonSerialize
@ApiModel(value = "OrderNo",description = "GTR订单表")
public class OrderNo {
    @TableId
    private String id;

    /**
     * 订单号
     */
    private String businessNo;

    /**
     * 集合订单id
     */
    private String mergeOrderId;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 订单类型：1=普通订单；2=集合订单
     */
    private String orderType;

    /**
     * dealer_code
     */
    private String dealerCode;

    /**
     * 配车时间
     */
    private String dispatchedDate;

    /**
     * 车辆到店时间
     */
    private String stockinTime;

    /**
     * 是否大客户订单：1=是；0=否
     */
    private String isBigCustomer;

    /**
     * 车主姓名
     */
    private String customerName;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 车型名称
     */
    private String seriesName;

    /**
     * 内饰色
     */
    private String trimColor;

    /**
     * 外观色
     */
    private String color;

    /**
     * 销售顾问
     */
    private String consultant;

    /**
     * 销售顾问id
     */
    private String consultantId;

    /**
     * 销售日期
     */
    private String sheetCreateDate;

    /**
     * 预约交车日
     */
    private String deliveringDate;

    /**
     * 交车完成日
     */
    private String finishDate;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * update_time
     */
    private Date updateTime;

}
