package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("scrm_dept")
public class DeptInfo {
    @TableId
    private Long id;
    private Long parentId;
    private String parentCode;
    private String parentName;
    private String areaName;
    private String areaCode;
    private String dealerCode;
    private String shortName;
    private String fullName;
    private Integer isDeleted;
    private Date createTime;
    private Date updateTime;

}
