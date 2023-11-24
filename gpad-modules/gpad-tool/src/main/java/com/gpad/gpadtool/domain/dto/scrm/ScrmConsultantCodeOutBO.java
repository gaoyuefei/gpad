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
public class ScrmConsultantCodeOutBO {

    @ApiModelProperty(value = "错误代码，200成功，其他都是失败")
    private String code;

    @ApiModelProperty(value = "异常描述")
    private String message;

    @ApiModelProperty(value = "data")
    private String data;

    @ApiModelProperty(value = "员工姓名")
    private String name;

    @ApiModelProperty(value = "企微头像")
    private String avatar;

    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @ApiModelProperty(value = "员工活码")
    private String qrcodeUrl;
}

