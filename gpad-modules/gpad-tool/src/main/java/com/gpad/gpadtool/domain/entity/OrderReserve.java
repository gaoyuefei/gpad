package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@Accessors(chain = true)
@TableName("order_reserve_info")
@JsonSerialize
@ApiModel(value = "OrderReserve",description = "GTR订单预留表")
public class OrderReserve {

    @TableId
    private String id;
    private String businessNo;
    private String orderType;
    private String reservationUser;
    private String reservationPhone;
    private String reservationDay;
    private String remark;
    private String createBy;
    private String updateBy;
    private Date createTime;
    private Date updateTime;
}
