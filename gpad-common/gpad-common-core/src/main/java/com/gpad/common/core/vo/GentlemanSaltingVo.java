package com.gpad.common.core.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @BelongsProject: gpad-api
 * @BelongsPackage: com.gpad.common.core.vo
 * @Author: LF
 * @CreateTime: 2023-09-25
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@Builder
public class GentlemanSaltingVo {

    @ApiModelProperty(value="时间戳")
    private Long ts;

    @ApiModelProperty(value="签名")
    private String sign;

    @ApiModelProperty(value="nonce")
    private String nonce;

}

