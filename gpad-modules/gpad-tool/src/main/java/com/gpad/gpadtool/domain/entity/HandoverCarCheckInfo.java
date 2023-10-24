package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("handover_car_check_info")
public class HandoverCarCheckInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    /**
     * id
     */
    private Long id;

    /**
     * 订单号
     */
    private String bussinessNo;

    /**
     * 订单类型（普通订单，集合订单）
     */
    private Integer orderType;

    /**
     * 交车确认客户名称
     */
    private String confirmUserName;


    /**
     * 交车确认客户名称
     */
    private String contractAplNo;

    /**
     * 交车确认客户手机号码
     */
    private String confirmUserPhone;

    /**
     * 车辆外观漆面正常无重整及凹痕，各部位颜色都正常
     */
    private Integer outPaintSurface;

    /**
     * 车辆玻璃无划痕及污渍
     */
    private Integer outGlass;

    /**
     * 车身灯光变化正常，外观无裂痕
     */
    private Integer outLight;

    /**
     * 雨刮器工作正常，无异响
     */
    private Integer outWiper;

    /**
     * 车辆内饰及仪表板颜色正常，无明显凹痕及污渍
     */
    private Integer inInterior;

    /**
     * 座椅平整无明显破损及污渍
     */
    private Integer inSeat;

    /**
     * 确认电动装置能正常工作（车门、车窗、天窗等）
     */
    private Integer inMotorUnit;

    /**
     * 其它车辆相关事宜
     */
    private Integer inOther;

    /**
     * 门窗开关及上锁方法（车门儿童安全锁等）
     */
    private Integer operateLock;

    /**
     * dvd语音电子导航系统说明及演示
     */
    private Integer operateDvd;

    /**
     * 驾驶位置的调整方法（座椅、方向盘）
     */
    private Integer operateDriverSeat;

    /**
     * 空调系统操作说明
     */
    private Integer operateAirCondition;

    /**
     * 外后视镜和内后视镜的调整方法
     */
    private Integer operateRearviewMirror;

    /**
     * 音响系统操作说明
     */
    private Integer operateSound;

    /**
     * 开关的操作方法（大灯、雾灯、转向灯、紧急指示灯等）
     */
    private Integer operateSwitch;

    /**
     * 轮胎/轮毂外观/胎压符合标准（含备胎）
     */
    private Integer operateTire;

    /**
     * 仪表盘及各项指示灯说明
     */
    private Integer operateDashboard;

    /**
     * 确认随车附件和工具（工具、千斤顶、点烟器等）
     */
    private Integer operateTools;

    /**
     * 车辆合格证
     */
    private Integer dataQualified;

    /**
     * 车辆一致性证书
     */
    private Integer dataConsistence;

    /**
     * 销售统一发票
     */
    private Integer dataInvoice;

    /**
     * 汽车三包凭证
     */
    private Integer dataSanbao;

    /**
     * 用户手册
     */
    private Integer dataUserManual;

    /**
     * 保修保养手册
     */
    private Integer dataMaintenance;

    /**
     * 车钥匙/遥控器*把
     */
    private Integer dataKeys;

    /**
     * 其他
     */
    private String dataOthers;

    /**
     * 销售顾问已介绍了服务部相关人员
     */
    private Integer serviceServers;

    /**
     * 已解释了保修政策及服务指南
     */
    private Integer serviceWarrantyPolicy;

    /**
     * 介绍车辆各种操作及性能
     */
    private Integer serviceProperty;

    /**
     * 销售顾问介绍客服中心面访
     */
    private Integer serviceCustomer;

    /**
     * 介绍24小时救援电话
     */
    private Integer serviceRescuePhone;

    /**
     * 约定首次保养日期：3个月或5000公里
     */
    private Integer serviceFirstMaintenance;

    /**
     * 签字类型（0=线上签署；1=线下签署）
     */
    private Integer signType;

    /**
     * 签字状态（0=未签署；1=已签署）
     */
    private Integer signStatus;

    /**
     * 签字状态（0=未签署；1=已签署）
     */
    private Integer delFlag;
    /**
     * 签字状态（0=未签署；1=已签署）
     */
    private Integer version;

    /**
     * create_by
     */
    private String createBy;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * update_by
     */
    private String updateBy;

    /**
     * update_time
     */
    private Date updateTime;

}
