package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmPdiFileListDto.java
 * @Description PDI文件列表
 * @createTime 2023年10月09日 15:43:00
 */
@Data
public class ScrmPdiFileListDto {
    @ApiModelProperty(value = "文件路径")
    private String fileUrl;

    @ApiModelProperty(value = "文件类型")
    private String fileType;

}
