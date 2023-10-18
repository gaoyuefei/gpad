package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderNoListParamVo.java
 * @Description 对接GRT查询订单列表入参Dto
 * @createTime 2023年09月22日 11:30:00
 */
@Data
@ApiModel(value = "OrderNoListParamVo",description = "对接GRT查询订单列表入参Dto")
public class OrderNoListParamVo {
    @ApiModelProperty(value = "登录账号")
    private String userCode;
    @ApiModelProperty(value = "店代码")
    private String dealerCode;
    @ApiModelProperty(value = "订单更新开始时间   yyyy-MM-dd hh24:mi:ss")
    private String updatedStart;
    @ApiModelProperty(value = "订单更新结束时间 yyyy-MM-dd hh24:mi:ss")
    private String updatedEnd;

}
