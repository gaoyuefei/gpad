package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-12-20
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class GoodsIdAndNewVIdVo {

    @ApiModelProperty(value = "图片集名称")
    private String goodsId;

    @ApiModelProperty(value = "图片集名称")
    private String newVehicleId;
}

