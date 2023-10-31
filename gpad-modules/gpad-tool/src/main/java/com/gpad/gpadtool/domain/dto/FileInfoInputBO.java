package com.gpad.gpadtool.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@ApiModel(value = "FileInfoInputBO",description = "文件BO")
public class FileInfoInputBO {

    @ApiModelProperty(value = "id")
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "订单号")
    private String bussinessNo;

    @ApiModelProperty(value = "当前登录账号")
    private String  productUsername;

    @ApiModelProperty(value = "文件类型")
    private String  fileType;

    @ApiModelProperty(value = "文件类型")
    private String  linkType;

    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

}
