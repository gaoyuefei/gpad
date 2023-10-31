package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad-api
 * @BelongsPackage: com.gpad.gpadtool.domain.dto
 * @Author: LF
 * @CreateTime: 2023-10-16
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class LoginReqVo {

    @ApiModelProperty(value = "id")
    private String username;

    @ApiModelProperty(value = "订单号")
    private String password;

}
