package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-11-10
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class MiseCdrListPadVo {

    @ApiModelProperty(value = "颜色集合")
    private String id;

    @ApiModelProperty(value = "颜色集合")
    private String code;

    @ApiModelProperty(value = "颜色集合")
    private String cdrType;

    @ApiModelProperty(value = "颜色集合")
    private String cdrVideo;
    @ApiModelProperty(value = "颜色集合")
    private String cdrVideoName;
    @ApiModelProperty(value = "颜色集合")
    private String coverImg;
    @ApiModelProperty(value = "颜色集合")
    private String coverImgList;
    @ApiModelProperty(value = "颜色集合")
    private String materialCode;
    @ApiModelProperty(value = "颜色集合")
    private String miseId;
    @ApiModelProperty(value = "颜色集合")
    private String photoGalleryId;
    @ApiModelProperty(value = "排序")
    private Integer sort;

}

