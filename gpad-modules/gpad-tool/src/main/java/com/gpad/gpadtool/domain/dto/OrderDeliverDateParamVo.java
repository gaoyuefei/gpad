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
@ApiModel(value = "OrderDeliverDateParamVo",description = "对接GRT修改订单列表入参Dto")
public class OrderDeliverDateParamVo {
    @ApiModelProperty(value =  "订单号")
    private String bussinessNo;

    @ApiModelProperty(value =  "预约交车时间 YY-MM-DD HH:mm:ss")
    private String deliveringDate;

}
