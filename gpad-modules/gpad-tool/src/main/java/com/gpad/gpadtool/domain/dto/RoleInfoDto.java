package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName RoleInfoDto.java
 * @Description 角色信息Dto
 * @createTime 2023年09月21日 15:51:00
 */
@Data
@ApiModel(value = "RoleInfoDto",description = "角色信息Dto")
public class RoleInfoDto {
    @ApiModelProperty(value =  "id")
    private Long id;
    @ApiModelProperty(value =  "父ID")
    private Long parentId;
    @ApiModelProperty(value =  "角色名称")
    private String roleName;
    @ApiModelProperty(value =  "角色别名")
    private String roleAlias;
    @ApiModelProperty(value =  "删除标识=0：否 1：是")
    private Integer isDeleted;
}
