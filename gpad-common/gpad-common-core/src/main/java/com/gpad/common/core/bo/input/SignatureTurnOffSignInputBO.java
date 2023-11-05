package com.gpad.common.core.bo.input;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.common.core.bo.input
 * @Author: LF
 * @CreateTime: 2023-11-05
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class SignatureTurnOffSignInputBO {

    @NotBlank
    @ApiModelProperty(value="状态")
    private String status;

    @NotBlank
    @ApiModelProperty(value = "订单编号")
    private String bussinessNo;

    @ApiModelProperty(value="id")
    private String id;

}

