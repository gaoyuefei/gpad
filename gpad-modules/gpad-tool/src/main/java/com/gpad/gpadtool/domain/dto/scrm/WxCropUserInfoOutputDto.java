package com.gpad.gpadtool.domain.dto.scrm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName WxCropUserInfoOutputDto.java
 * @Description 企微成员信息
 * @createTime 2023年10月09日 17:01:00
 */
@Data
public class WxCropUserInfoOutputDto {

    @ApiModelProperty(value = "帐号")
    private String userId;

    @ApiModelProperty(value = "手机")
    private String mobile;

    //对应SCRM中的realName
    @ApiModelProperty(value = "成员名称")
    private String name;

    //对应SCRM中的dealerCode
    @ApiModelProperty(value = "门店编码")
    private String deptCode;

    //对应SCRM中的account账号
    @ApiModelProperty(value = "工号")
    private String employeeNo;

    @ApiModelProperty(value = "部门")
    private String deptName;

    @ApiModelProperty(value = "职务")
    private String position;
}
