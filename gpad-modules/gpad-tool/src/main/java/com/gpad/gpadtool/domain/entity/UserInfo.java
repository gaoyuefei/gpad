package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("scrm_user")
public class UserInfo {
    @TableId
    private Long id;
    private String account;
    private String name;
    private String realName;
    private Integer sex;
    private String roleId;
    private String dealerCode;
    private String dealerData;
    private Integer isDeleted;
    private Date createTime;
    private Date updateTime;
    private String wxworkUserId;
    private Date syncTime;
    private Date syncUpdateTime;

}
