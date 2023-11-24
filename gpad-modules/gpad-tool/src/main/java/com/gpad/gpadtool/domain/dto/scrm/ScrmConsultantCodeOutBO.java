package com.gpad.gpadtool.domain.dto.scrm;

import com.gpad.gpadtool.domain.vo.ScrmConsultantCodeVo;
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
    private ScrmConsultantCodeVo data;

}

