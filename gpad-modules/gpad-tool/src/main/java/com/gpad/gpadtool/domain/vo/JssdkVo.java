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
public class JssdkVo {

    @ApiModelProperty(value = "微信号ID")
    private String appId;

    @ApiModelProperty(value = "时间戳")
    private String timestamp;

    @ApiModelProperty(value = "生成随机串")
    private String nonceStr;

    @ApiModelProperty(value = "签名")
    private String signature;
}

