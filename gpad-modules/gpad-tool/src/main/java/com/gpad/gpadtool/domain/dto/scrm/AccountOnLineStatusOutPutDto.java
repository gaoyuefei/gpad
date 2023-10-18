package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName AccountOnLineStatusOutPutDto.java
 * @Description 账号登录状态验证接口出参
 * @createTime 2023年10月09日 16:33:00
 */
@Data
public class AccountOnLineStatusOutPutDto {
    @ApiModelProperty(value = "成功标识(1:成功  0:失败)")
    private String resultCode;

    @ApiModelProperty(value = "信息标识(成功则返回“成功”失败则附带失败信息)")
    private String resultMessage;
}
