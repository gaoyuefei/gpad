package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName FileInfoDto.java
 * @Description 文件Dto
 * @createTime 2023年09月21日 16:00:00
 */
@Data
@ApiModel(value = "FileInfoOutBo",description = "文件Dto")
public class FileInfoOutBo {

    private String id;

    @ApiModelProperty(value =  "适用车型")
    private String suitCarType;

    @ApiModelProperty(value =  "客户名字")
    private String customerName;

    @ApiModelProperty(value =  "客户名字")
    private List<FileInfoDto> fileInfoDto;

}
