package com.gpad.gpadtool.domain.dto.wxapi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.dto.wxapi
 * @Author: LF
 * @CreateTime: 2023-11-10
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class ExhibitionMixPadInputBO {

    @NotBlank
    @ApiModelProperty(value =  "派生代码")
    private String packageCode;

}

