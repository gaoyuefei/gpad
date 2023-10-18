package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName SyncScrmRoleInfoParamVo.java
 * @Description 对接SCRM同步角色数据入参
 * @createTime 2023年09月22日 10:32:00
 */
@Data
@ApiModel(value = "SyncScrmRoleInfoParamVo",description = "对接SCRM同步角色数据入参")
public class SyncScrmRoleInfoParamVo {

    @ApiModelProperty(value =  "当前页码")
    private Integer pageNo;
    @ApiModelProperty(value =  "总条数")
    private Integer totalNum;
    @ApiModelProperty(value =  "数据集合")
    private List<ScrmRoleInfoDto> dataInfo;
}
