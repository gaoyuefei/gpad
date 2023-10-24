package com.gpad.gpadtool.domain.entity;

import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("handover_car_flow_info")
public class FlowInfo {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String bussinessNo;
    private Integer orderType;
    private Integer nodeNum;
    private Integer version;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
}
