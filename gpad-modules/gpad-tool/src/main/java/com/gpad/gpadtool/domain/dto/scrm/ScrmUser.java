package com.gpad.gpadtool.domain.dto.scrm;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmUser.java
 * @Description 对接SCRM同步的用户信息
 * @createTime 2023年09月21日 11:36:00
 */
@Data
@Accessors(chain = true)
@TableName("scrm_user")
@JsonSerialize
@ApiModel(value = "ScrmUser",description = "对接SCRM同步的用户信息")
public class ScrmUser implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "账号")
    private String account;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "真实名称")
    private String realName;
    @ApiModelProperty(value = "性别")
    private Integer sex;
    @ApiModelProperty(value = "角色id")
    private String roleId;
    @ApiModelProperty(value = "部门id")
    private String dealerCode;
    @ApiModelProperty(value = "门店数据权限")
    private String dealerData;
    @ApiModelProperty(value = "是否已删除==0：否 1：是")
    private Integer isDeleted;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "微信USERid")
    private String wxworkUserId;
    @ApiModelProperty(value = "同步数据时间")
    private Date syncTime;
    @ApiModelProperty(value = "更新数据时间")
    private Date syncUpdateTime;

}
