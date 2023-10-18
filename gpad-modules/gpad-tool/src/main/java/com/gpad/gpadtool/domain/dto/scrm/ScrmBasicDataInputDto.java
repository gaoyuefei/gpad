package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmBasicDataInputDto.java
 * @Description TODO
 * @createTime 2023年09月26日 16:03:00
 */
@Data
public class ScrmBasicDataInputDto {
    @ApiModelProperty(value = "获取数据类型 必传（1、用户数据 2、角色数据 3、部门数据）")
    private String type;
}
