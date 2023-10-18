package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName UserInfoDto.java
 * @Description TODO
 * @createTime 2023年09月21日 15:42:00
 */
@Data
@ApiModel(value = "UserInfoDto",description = "用户信息Dto")
public class UserInfoDto {
    @ApiModelProperty(value =  "id")
    private Long id;
    @ApiModelProperty(value = "账号")
    private String account;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "真实名称")
    private String realName;
    @ApiModelProperty(value = "性别")
    private Integer sex;
    @ApiModelProperty(value = "角色id，多个角色id用,分隔")
    private String roleId;
    @ApiModelProperty(value = "部门id")
    private String dealerCode;
    @ApiModelProperty(value = "门店数据权限,巡回员用户(scrm,云外呼)可查看的门店数据(门店编码以,区分)")
    private String dealerData;
    @ApiModelProperty(value = "是否已删除;0：否 1：是")
    private Integer isDeleted;
    @ApiModelProperty(value = "SCRM创建时间")
    private Date createTime;
    @ApiModelProperty(value = "SCRM更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "微信USERid")
    private String wxworkUserId;
    @ApiModelProperty(value = "同步时间")
    private Date syncTime;
    @ApiModelProperty(value = "更新时间")
    private Date syncUpdateTime;
}
