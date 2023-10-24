package com.gpad.gpadtool.domain.dto;


import com.gpad.gpadtool.constant.PageCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderListByPageVo.java
 * @Description TODO
 * @createTime 2023年09月22日 16:22:00
 */
@Data
@ApiModel(value = "OrderListByPageVo",description = "分页条件查询订单列表入参")
public class OrderListByPageVo extends PageCondition {

    @ApiModelProperty(value =  "预计交车日开始日期")
    private String reservationHandoverDateStart;
    @ApiModelProperty(value =  "预计完成日开始日期")
    private String finishHandoverDateStart;
    @ApiModelProperty(value =  "预计交车日结束日期")
    private String reservationHandoverDateEnd;
    @ApiModelProperty(value =  "预计完成日结束日期")
    private String finishHandoverDateEnd;
    @ApiModelProperty(value =  "订单状态")
    private String orderStatus;
    @ApiModelProperty(value =  "车主姓名")
    private String customerName;
    @ApiModelProperty(value =  "手机号")
    private String mobilePhone;
    @ApiModelProperty(value =   "销售日期开始日期")
    private String sheetCreateDateStart;
    @ApiModelProperty(value =  "销售日期结束日期")
    private String sheetCreateDateEnd;
    @ApiModelProperty(value =  "是否超时")
    private String isOverTime;
    @ApiModelProperty(value =  "是否大客户")
    private String isBigCustomer;


}