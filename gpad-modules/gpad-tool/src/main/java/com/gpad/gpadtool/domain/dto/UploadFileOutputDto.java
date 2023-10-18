package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName UploadFileOutputDto.java
 * @Description 上传文档出参
 * @createTime 2023年10月07日 16:44:00
 */
@Data
public class UploadFileOutputDto {

    @ApiModelProperty(value =  "id")
    private String fileName;

    @ApiModelProperty(value =  "id")
    private String path;
}
