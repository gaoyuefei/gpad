package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-11-06
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class JzqDataValidSignatureVo {

    @ApiModelProperty(value = "data")
    private String message;

    @ApiModelProperty(value = "key")
    private Boolean valid;

    @ApiModelProperty(value = "data")
    private String seqNo;

    @ApiModelProperty(value = "key")
    private String code;
}

