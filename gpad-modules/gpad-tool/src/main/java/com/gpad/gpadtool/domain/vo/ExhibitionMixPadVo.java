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
public class ExhibitionMixPadVo {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "实拍分类名称")
    private String name;

    @ApiModelProperty(value = "素材类型 1 图片 2 视频")
    private String cdrType;

    @ApiModelProperty(value = "颜色集合")
    private List<CarColorListPadVo> carColorList;

    @ApiModelProperty(value = "图片素材集")
    private List<ExhibitionPhotoGalleryListPadVo> exhibitionPhotoGalleryList;

    @ApiModelProperty(value = "实拍视频集合 （cdrType=2时读取此字段展示视频）")
    private List<MiseVideoListPadVo> miseVideoList;


}

