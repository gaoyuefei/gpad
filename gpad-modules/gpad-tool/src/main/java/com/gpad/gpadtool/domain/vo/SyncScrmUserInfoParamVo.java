package com.gpad.gpadtool.domain.vo;

import com.gpad.gpadtool.domain.dto.scrm.ScrmUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName SyncScrmUserInfoParamVo.java
 * @Description 对接SCRM同步用户数据入参
 * @createTime 2023年09月21日 11:23:00
 */
@Data
@ApiModel(value = "SyncScrnUserInfoParamVo",description = "对接SCRM同步用户数据入参")
public class SyncScrmUserInfoParamVo {

    @ApiModelProperty(value = "当前页码")
    private Integer pageNo;
    @ApiModelProperty(value = "总条数")
    private Integer totalNum;
    @ApiModelProperty(value = "数据集合")
    private List<ScrmUser> dataInfo;

}