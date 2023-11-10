package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.vo
 * @Author: LF
 * @CreateTime: 2023-11-10
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class MiseVideoListPadVo {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "素材类型 1 图片 2 视频")
    private String cdrType;

    @ApiModelProperty(value = "素材视频")
    private String cdrVideo;

    @ApiModelProperty(value = "素材视频名称")
    private String cdrVideoName;

    @ApiModelProperty(value = "视频封面/素材图片")
    private String coverImg;

    @ApiModelProperty(value = "频封面/素材图片")
    private String coverImgList;

    @ApiModelProperty(value = "颜色集合")
    private String materialCode;

    @ApiModelProperty(value = "实拍分类id")
    private String miseId;

    @ApiModelProperty(value = "图片集id")
    private String photoGalleryId;
}

