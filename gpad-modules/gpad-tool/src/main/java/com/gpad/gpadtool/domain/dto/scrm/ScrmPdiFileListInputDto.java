package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmPdiFileListInputDto.java
 * @Description 根据订单编号查询PDI接口入参
 * @createTime 2023年09月26日 17:29:00
 */
@Data
public class ScrmPdiFileListInputDto {
    @ApiModelProperty(value = "vin")
    private String vin;
}
