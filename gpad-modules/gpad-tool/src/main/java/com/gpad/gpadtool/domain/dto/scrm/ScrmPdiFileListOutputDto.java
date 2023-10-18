package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmPdiFileListOutputDto.java
 * @Description 根据订单编号查询PDI接口出参
 * @createTime 2023年10月09日 15:42:00
 */
@Data
public class ScrmPdiFileListOutputDto {
    @ApiModelProperty(value = "文件路径")
    private List<ScrmPdiFileListDto> fileList;

    @ApiModelProperty(value = "状态(1001：待分配 1002：待完成 1003：已完成)")
    private String status;

    @ApiModelProperty(value = "成功标识(1:成功  0:失败)")
    private String resultCode;

    @ApiModelProperty(value = "信息标识(成功则返回“成功”失败则附带失败信息)")
    private String resultMessage;

}
