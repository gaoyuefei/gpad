package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("handover_car_flow_info")
public class FlowInfo {

    @TableId
    private Long id;
    private String businessNo;
    private Integer orderType;
    private Integer nodeNum;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
}
