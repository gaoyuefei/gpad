package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName BaseGrtResultDto.java
 * @Description TODO
 * @createTime 2023年09月22日 14:17:00
 */
@Data
@ApiModel(value = "BaseGrtResultDto",description = "GRT接口返回基类Dto")
public class BaseGrtResultDto {
    @ApiModelProperty(value =  "成功：200；异常：500")
    private String status;
    @ApiModelProperty(value =  "返回信息")
    private String message;
}
