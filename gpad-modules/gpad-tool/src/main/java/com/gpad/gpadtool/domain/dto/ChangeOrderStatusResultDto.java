package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ChangeOrderStatusResultDto.java
 * @Description 对接GRT变更订单状态返回Dto
 * @createTime 2023年09月22日 14:09:00
 */
@Data
@ApiModel(value = "ChangeOrderStatusResultDto",description = "对接GRT变更订单状态返回Dto")
public class ChangeOrderStatusResultDto {
    @ApiModelProperty(value =  "成功：200；异常：500")
    private String status;
    @ApiModelProperty(value =  "返回信息  异常时必填")
    private String message;
    @ApiModelProperty(value =  "数据，有则返回")
    private Object data;

    public ChangeOrderStatusResultDto() {
    }

    public ChangeOrderStatusResultDto(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ChangeOrderStatusResultDto(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
}
