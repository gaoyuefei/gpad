package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-11-10
 * @Description: TODO
 * @Version: 1.0
 */
public class CarColorListPadVo {

    @ApiModelProperty(value = "颜色集合")
    private String id;

    @ApiModelProperty(value = "颜色集合")
    private String code;

    @ApiModelProperty(value = "key")
    private Boolean colorDescribe;

    @ApiModelProperty(value = "data")
    private List<String> colorImgList;

    @ApiModelProperty(value = "colorImgs")
    private String colorImgs;

    @ApiModelProperty(value = "商品颜色名称")
    private String colorName;

    @ApiModelProperty(value = "colorType")
    private Integer colorType;

    @ApiModelProperty(value = "颜图标图片")
    private String imageUrl;

    @ApiModelProperty(value = "颜色置灰标识 false不置灰 true置灰")
    private String invalidFlag;

    @ApiModelProperty(value = "素材CODE")
    private String materialCode;

    @ApiModelProperty(value = "实拍分类id")
    private Integer miseId;

    @ApiModelProperty(value = "颜色名称")
    private Integer name;

    @ApiModelProperty(value = "是否显示开关")
    private String showFlag;

    @ApiModelProperty(value = "显示名称")
    private String showName;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}

