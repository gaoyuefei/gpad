package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName SyncScrmDeptInfoParamVo.java
 * @Description 对接SCRM同步部门数据入参
 * @createTime 2023年09月22日 10:38:00
 */
@Data
@ApiModel(value = "SyncScrmDeptInfoParamVo",description = "对接SCRM同步部门数据入参")
public class SyncScrmDeptInfoParamVo {

    @ApiModelProperty(value =  "当前页码")
    private Integer pageNo;
    @ApiModelProperty(value =  "总条数")
    private Integer totalNum;
    @ApiModelProperty(value =  "数据集合")
    private List<ScrmDeptInfoDto> dataInfo;
}
