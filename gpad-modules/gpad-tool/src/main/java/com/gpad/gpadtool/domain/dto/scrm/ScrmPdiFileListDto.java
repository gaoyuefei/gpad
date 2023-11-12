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
   //2023/11/12 转成 1图片 2视频  3PDF
    @ApiModelProperty(value = "1：PDF  2：图片  3：视频")
    private String fileType;

}
