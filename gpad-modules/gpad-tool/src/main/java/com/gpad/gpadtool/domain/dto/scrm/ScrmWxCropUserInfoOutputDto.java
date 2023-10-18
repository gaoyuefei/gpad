package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmUserInfoToInstrumentOutputDto.java
 * @Description SCRM员工信息出参
 * @createTime 2023年10月09日 16:56:00
 */
@Data
public class ScrmWxCropUserInfoOutputDto {
    @ApiModelProperty(value = "错误代码，200成功，其他都是失败")
    private String code;

    @ApiModelProperty(value = "异常描述")
    private String message;

    @ApiModelProperty(value = "数据，成功则返回")
    private WxCropUserInfoOutputDto data;

    @ApiModelProperty(value = "成功标识")
    private String success;

    @ApiModelProperty(value = "错误原因")
    private String msg;
}
