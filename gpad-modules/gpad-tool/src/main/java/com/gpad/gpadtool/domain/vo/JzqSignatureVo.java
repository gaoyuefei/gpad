package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-11-04
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class JzqSignatureVo {

    @ApiModelProperty(value = "data")
    private Boolean success;

    @ApiModelProperty(value = "key")
    private String msg;

    @ApiModelProperty(value = "data")
    private String resultCode;

    @ApiModelProperty(value = "key")
    private String data;
}

