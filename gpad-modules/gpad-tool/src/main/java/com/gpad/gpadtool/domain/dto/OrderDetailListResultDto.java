package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderDetailListResultDto.java
 * @Description TODO
 * @createTime 2023年09月22日 11:49:00
 */
@Data
@ApiModel(value = "OrderDetailListResultDto", description = "GRT待交车订单详情列表Dto")
public class OrderDetailListResultDto {
    @ApiModelProperty(value = "成功：200；异常：500")
    private String status;
    @ApiModelProperty(value = "返回信息")
    private String message;
    @ApiModelProperty(value = "数据")
    private List<OrderDetailResultDto> data;
}
