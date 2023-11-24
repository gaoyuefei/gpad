package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-11-24
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class ScrmConsultantCodeVo {

    @ApiModelProperty(value = "员工姓名")
    private String name;

    @ApiModelProperty(value = "企微头像")
    private String avatar;

    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @ApiModelProperty(value = "员工活码")
    private String qrcodeUrl;
}

