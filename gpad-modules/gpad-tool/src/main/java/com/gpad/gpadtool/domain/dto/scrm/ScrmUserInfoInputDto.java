package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmUserInfoInputDto.java
 * @Description SCRM员工信息入参
 * @createTime 2023年10月09日 10:22:00
 */
@Data
public class ScrmUserInfoInputDto {
    @ApiModelProperty(value = "员工编号")
    private String account;
}
