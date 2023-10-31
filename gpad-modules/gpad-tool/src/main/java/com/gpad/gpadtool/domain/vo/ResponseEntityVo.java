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
public class ResponseEntityVo {

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "data")
    private String data;

    @ApiModelProperty(value = "success")
    private String success;

    @ApiModelProperty(value = "msg")
    private String msg;
}

