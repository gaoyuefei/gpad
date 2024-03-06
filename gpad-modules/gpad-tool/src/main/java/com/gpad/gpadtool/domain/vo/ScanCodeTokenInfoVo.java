package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-11-01
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class ScanCodeTokenInfoVo {

    @ApiModelProperty(value = "msg")
    private String msg;

    @ApiModelProperty(value = "状态值由枚举类映射")
    private String code;

    @ApiModelProperty(value = "express_time")
    private String expressTime;

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "销售顾问信息")
    private SalesConsultantVo salesConsultantVo;
}

