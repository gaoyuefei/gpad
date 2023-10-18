package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmWxCropUserInfoInputDto.java
 * @Description 获取企微成员信息接口入参
 * @createTime 2023年10月09日 10:36:00
 */
@Data
public class ScrmWxCropUserInfoInputDto {
    @ApiModelProperty(value = "企微成员userId")
    private String userId;
}
