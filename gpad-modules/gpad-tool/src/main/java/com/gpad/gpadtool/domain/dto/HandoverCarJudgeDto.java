package com.gpad.gpadtool.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "HandoverCarJudgeDto",description = "交车准备Dto")
public class HandoverCarJudgeDto {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "订单号")
    private String bussinessNo;
    @ApiModelProperty(value = "满意度：1--5")
    private Integer satisfaction;
    @ApiModelProperty(value = "APP售后权益是否激活：1是0否")
    private Integer hasAfterSalesBenefits;
    @ApiModelProperty(value = "讲解是否清楚：1是0否")
    private Integer isClear;
    @ApiModelProperty(value = "是否愿意推荐：1是0否")
    private Integer isRecommend;
    @ApiModelProperty(value = "客户id")
    private Long userId;
    @ApiModelProperty(value = "客户编码")
    private String userCode;
    @ApiModelProperty(value = "客户名称")
    private String userName;
    private Date createTime;

}
