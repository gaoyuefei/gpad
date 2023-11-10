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
public class ExhibitionPhotoGalleryListPadVo {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "图片集素材图集合")
    private MiseCdrListPadVo miseCdrList;

    @ApiModelProperty(value = "图片集名称")
    private Boolean name;

    @ApiModelProperty(value = "排序")
    private Integer sort;

}

