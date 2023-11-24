package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.dto.scrm
 * @Author: LF
 * @CreateTime: 2023-11-24
 * @Description: 获取顾客首页活码
 * @Version: 1.0
 */
@Data
public class ScrmConsultantCodeInputBO {

    @ApiModelProperty(value = "data")
    private String data;

    @ApiModelProperty(value = "username")
    private String username;
}

