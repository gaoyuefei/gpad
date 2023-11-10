package com.gpad.gpadtool.domain.dto.wxapi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.dto.wXApiCommentOutBO
 * @Author: LF
 * @CreateTime: 2023-11-09
 * @Description: 微信评价BO
 * @Version: 1.0
 */
@Data
@ApiModel(value = "wXApiCommentOutBO",description = "交车评价参数BO")
public class WxApiCommentInputBO {

    @NotBlank
    @ApiModelProperty(value =  "订单号")
    private String orderNo;

    @NotBlank
    @ApiModelProperty(value =  "vin")
    private String vin;
}

