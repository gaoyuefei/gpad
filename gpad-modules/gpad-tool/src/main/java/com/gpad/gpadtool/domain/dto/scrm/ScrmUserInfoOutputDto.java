package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmUserInfoOutputDto.java
 * @Description SCRM员工信息
 * @createTime 2023年10月09日 16:57:00
 */
@Data
public class ScrmUserInfoOutputDto {
    @ApiModelProperty(value = "员工id")
    private Long id;

    @ApiModelProperty(value = "员工编号")
    private String account;

    @ApiModelProperty(value = "员工姓名")
    private String realName;

    @ApiModelProperty(value = "角色id（用，隔开）")
    private String roleId;

    @ApiModelProperty(value = "角色名称（用，隔开）")
    private String roleName;

    @ApiModelProperty(value = "门店编码")
    private String dealerCode;
}
