package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderNoListResultDto.java
 * @Description TODO
 * @createTime 2023年09月22日 11:35:00
 */
@Data
@ApiModel(value = "OrderNoListResultDto",description = "GRT待交车订单号列表Dto")
public class OrderNoListResultOutBo {

    @ApiModelProperty(value = "总数")
    private String total;

    @ApiModelProperty(value = "pageSize")
    private String pageSize;

    @ApiModelProperty(value = "pageNum")
    private String pageNum;

    @ApiModelProperty(value = "数据")
    private List<OrderNoResultDto> data;
}
