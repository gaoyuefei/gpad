package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-11-09
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class WxApiGetCommentVo {


    @ApiModelProperty(value = "订单类型;1-020 6-直订 7-创新营销 10-线下")
    private String orderType;

    @ApiModelProperty(value = "评论内容")
    private String commentResult;

    @ApiModelProperty(value = "是否可评论;0-否 1-是")
    private Boolean isCanComment;

    @ApiModelProperty(value = "是否已评论;0-否 1-是")
    private Boolean isComment;

    @ApiModelProperty(value = "订单号")
    private String orderNo;
}

