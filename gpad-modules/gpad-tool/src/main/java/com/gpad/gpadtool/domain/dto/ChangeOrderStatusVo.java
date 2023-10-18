package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ChangeOrderStatusVo.java
 * @Description TODO
 * @createTime 2023年09月22日 14:10:00
 */
@Data
@ApiModel(value = "ChangeOrderStatusVo",description = "对接GRT接收订单状态变更Dto")
public class ChangeOrderStatusVo {
    @ApiModelProperty(value =  "订单号")
    private String businessNo;
    @ApiModelProperty(value =  "订单状态")
    private String orderStatus;
}
