package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-10-31
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class WxTokenVO {

    @ApiModelProperty(value = "data")
    private String token;

    @ApiModelProperty(value = "key")
    private String expirationTime;
}

