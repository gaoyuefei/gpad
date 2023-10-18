package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName AccessTokenResultDto.java
 * @Description TODO
 * @createTime 2023年09月21日 15:37:00
 */
@Data
@ApiModel(value = "AccessTokenResultDto",description = "accessToken返回Dto")
public class AccessTokenResultDto {

    @ApiModelProperty(value = "错误码 为0表示成功，非0表示调用失败")
    private Integer errcode;
    @ApiModelProperty(value = "错误信息")
    private String errmsg;
    @ApiModelProperty(value = "过期时间 单位：秒")
    private Integer expiresIn;
    @ApiModelProperty(value = "获取到的凭证，最长为512字节")
    private String accessToken;
}
