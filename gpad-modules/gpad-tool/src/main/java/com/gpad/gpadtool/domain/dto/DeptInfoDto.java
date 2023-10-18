package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName DeptInfoDto.java
 * @Description 部门信息Dto
 * @createTime 2023年09月21日 15:52:00
 */
@Data
@ApiModel(value = "UserInfoDto",description = "部门信息Dto")
public class DeptInfoDto {
    @ApiModelProperty(value =  "id")
    private Long id;
    @ApiModelProperty(value =  "父级id")
    private Long parentId;
    @ApiModelProperty(value =  "父级编码")
    private String parentCode;
    @ApiModelProperty(value =  "父级名称")
    private String parentName;
    @ApiModelProperty(value =  "大区名称")
    private String areaName;
    @ApiModelProperty(value =  "大区编码")
    private String areaCode;
    @ApiModelProperty(value =  "门店编码")
    private String dealerCode;
    @ApiModelProperty(value =  "门店简称")
    private String shortName;
    @ApiModelProperty(value =  "部门全称")
    private String fullName;
    @ApiModelProperty(value =  "是否已删除;0：否 1：是")
    private Integer isDeleted;
    @ApiModelProperty(value =  "创建时间")
    private Date createTime;
    @ApiModelProperty(value =  "更新时间")
    private Date updateTime;
}
