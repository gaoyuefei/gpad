package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName SyncScrmInfoResultDto.java
 * @Description 对接SCRM同步用户数据返回Dto
 * @createTime 2023年09月22日 10:27:00
 */
@Data
@ApiModel(value = "SyncScrmInfoResultDto",description = "对接SCRM同步用户数据返回Dto")
public class SyncScrmInfoResultDto {
    @ApiModelProperty(value =  "错误码 为0表示成功，非0表示调用失败")
    private Integer code;
    @ApiModelProperty(value =  "错误信息")
    private String msg;
    @ApiModelProperty(value =  "数据，有则返回")
    private Object data;

    public SyncScrmInfoResultDto() {
    }

    public SyncScrmInfoResultDto(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public SyncScrmInfoResultDto success(){
        return new SyncScrmInfoResultDto(0,"",null);
    }
}
