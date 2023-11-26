package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderNoListParamVo.java
 * @Description 对接GRT查询订单列表入参Dto
 * @createTime 2023年09月22日 11:30:00
 */
@Data
@ApiModel(value = "FilePathCheckInputBO",description = "对接GRT修改订单列表入参Dto")
public class JzqContractFileStreamInputBO {


    @ApiModelProperty(value =  "主键ID")
    private String id;

    @NotBlank
    @ApiModelProperty(value =  "订单号")
    private String bussinessNo;


    @ApiModelProperty(value =  "文件状态")
    private String fileType;

    @NotBlank
    @ApiModelProperty(value =  "文件关联类型")
    private String linkType;

    @NotBlank
    @ApiModelProperty(value =  "合同编号")
    private String contractAplNo;


}
