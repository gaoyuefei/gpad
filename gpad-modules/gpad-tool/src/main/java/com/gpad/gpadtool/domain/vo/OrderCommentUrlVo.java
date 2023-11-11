package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-11-11
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class OrderCommentUrlVo {
    //type=0&id=741093587770&channel=small_channel

    @ApiModelProperty(value =  "订单号")
    private String type;

    @ApiModelProperty(value =  "vin")
    private String id;

    @ApiModelProperty(value =  "订单号")
    private String channel;

    @ApiModelProperty(value =  "vin")
    private String orderNo;

}

