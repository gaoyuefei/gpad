package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-11-12
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class JzqOrganizationAuditStatusVo {

    @ApiModelProperty(value = "成功true；异常false")
    private Boolean success;

    @ApiModelProperty(value = "msg")
    private String msg;

    @ApiModelProperty(value = "resultCode")
    private String resultCode;

    @ApiModelProperty(value = "data")
    private JzqStatusDataVo data;
}

