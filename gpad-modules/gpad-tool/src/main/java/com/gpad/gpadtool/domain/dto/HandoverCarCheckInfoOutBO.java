package com.gpad.gpadtool.domain.dto;


import com.gpad.gpadtool.domain.entity.GpadIdentityAuthInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "HandoverCarCheckInfoOutBO",description = "交车确认Dto")
public class HandoverCarCheckInfoOutBO {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "账号ID")
    private String  accountId;

    @ApiModelProperty(value = "订单号")
    private String bussinessNo;
    @ApiModelProperty(value = "订单类型（普通订单，集合订单）")
    private Integer orderType;
    @ApiModelProperty(value =  "交车确认客户名称")
    private String confirmUserName;
    @ApiModelProperty(value = "交车确认客户手机号码")
    private String confirmUserPhone;
    @ApiModelProperty(value = "车辆外观漆面正常无重整及凹痕，各部位颜色都正常")
    private Integer outPaintSurface;
    @ApiModelProperty(value = "车辆玻璃无划痕及污渍")
    private Integer outGlass;
    @ApiModelProperty(value = "车身灯光变化正常，外观无裂痕")
    private Integer outLight;
    @ApiModelProperty(value = "雨刮器工作正常，无异响")
    private Integer outWiper;
    @ApiModelProperty(value = "车辆内饰及仪表板颜色正常，无明显凹痕及污渍")
    private Integer inInterior;
    @ApiModelProperty(value = "座椅平整无明显破损及污渍")
    private Integer inSeat;
    @ApiModelProperty(value = "确认电动装置能正常工作（车门、车窗、天窗等）")
    private Integer inMotorUnit;
    @ApiModelProperty(value = "其它车辆相关事宜")
    private Integer inOther;
    @ApiModelProperty(value = "门窗开关及上锁方法（车门儿童安全锁等）")
    private Integer operateLock;
    @ApiModelProperty(value = "DVD语音电子导航系统说明及演示")
    private Integer operateDvd;
    @ApiModelProperty(value = "驾驶位置的调整方法（座椅、方向盘）")
    private Integer operateDriverSeat;
    @ApiModelProperty(value = "空调系统操作说明")
    private Integer operateAirCondition;
    @ApiModelProperty(value = "外后视镜和内后视镜的调整方法")
    private Integer operateRearviewMirror;
    @ApiModelProperty(value = "音响系统操作说明")
    private Integer operateSound;
    @ApiModelProperty(value = "开关的操作方法（大灯、雾灯、转向灯、紧急指示灯等）")
    private Integer operateSwitch;
    @ApiModelProperty(value = "轮胎/轮毂外观/胎压符合标准（含备胎）")
    private Integer operateTire;
    @ApiModelProperty(value = "仪表盘及各项指示灯说明")
    private Integer operateDashboard;
    @ApiModelProperty(value = "确认随车附件和工具（工具、千斤顶、点烟器等）")
    private Integer operateTools;
    @ApiModelProperty(value = "车辆合格证")
    private Integer dataQualified;
    @ApiModelProperty(value = "车辆一致性证书")
    private Integer dataConsistence;
    @ApiModelProperty(value = "销售统一发票")
    private Integer dataInvoice;
    @ApiModelProperty(value = "汽车三包凭证")
    private Integer dataSanbao;
    @ApiModelProperty(value = "用户手册")
    private Integer dataUserManual;
    @ApiModelProperty(value = "保修保养手册")
    private Integer dataMaintenance;
    @ApiModelProperty(value = "车钥匙/遥控器*把")
    private Integer dataKeys;
    @ApiModelProperty(value = "其他")
    private String dataOthers;
    @ApiModelProperty(value = "销售顾问已介绍了服务部相关人员")
    private Integer serviceServers;
    @ApiModelProperty(value = "已解释了保修政策及服务指南")
    private Integer serviceWarrantyPolicy;
    @ApiModelProperty(value = "介绍车辆各种操作及性能")
    private Integer serviceProperty;
    @ApiModelProperty(value = "销售顾问介绍客服中心面访")
    private Integer serviceCustomer;
    @ApiModelProperty(value = "介绍24小时救援电话")
    private Integer serviceRescuePhone;
    @ApiModelProperty(value = "约定首次保养日期：3个月或5000公里")
    private Integer serviceFirstMaintenance;
    @ApiModelProperty(value = "合同连接")
    private String contractLink;
    @ApiModelProperty(value = "签字类型（0=线上签署；1=线下签署）")
    private Integer signType;
    @ApiModelProperty(value = "签字状态（0=未签署；，1=已签署）")
    private Integer signStatus;
    @ApiModelProperty(value = "产品专家记忆签名")
    private String  memorySignPath;
    @ApiModelProperty(value = "是否实名 0 ： 1：")
    private Integer realName;

    @ApiModelProperty(value = "0：未完成交车，1：已完成交车")
    private Integer isDelivery;

    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    @ApiModelProperty(value = "订单详情实体返参")
    private OrderDetailResultDto orderDetailResultDto;

    @ApiModelProperty(value = "文件类型返参")
    private GpadIdentityAuthInfo gpadIdentityAuthInfo;

}
