package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2024-01-09
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class JssdkTicketVO {

    @ApiModelProperty(value = "成功0")
    private String errcode;

    @ApiModelProperty(value = "ok")
    private String errmsg;

    @ApiModelProperty(value = "生成签名所需的jsapi_ticket，最长为512字节")
    private String ticket;

    @ApiModelProperty(value = "凭证的有效时间（秒）")
    private String expires_in;
}

