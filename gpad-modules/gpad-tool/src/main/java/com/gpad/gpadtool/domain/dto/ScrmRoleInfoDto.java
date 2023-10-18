package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmRoleInfoDto.java
 * @Description 对接SCRM同步的角色信息
 * @createTime 2023年09月22日 10:33:00
 */
@Data
@ApiModel(value = "ScrmRoleInfoDto",description = "对接SCRM同步的角色信息")
public class ScrmRoleInfoDto {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value =  "父级id")
    private Long parentId;
    @ApiModelProperty(value =  "角色名")
    private String roleName;
    @ApiModelProperty(value =  "角色别名")
    private String roleAlias;
    @ApiModelProperty(value =  "是否已删除==0：否 1：是")
    private Integer isDeleted;
}
