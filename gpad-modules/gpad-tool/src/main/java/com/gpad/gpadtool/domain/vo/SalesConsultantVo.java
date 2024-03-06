package com.gpad.gpadtool.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Godfrey
 * @CreateTime 2024-3-6 11:20:58
 * @Description 销售顾问信息
 * @Version 1.0
 */
@Data
public class SalesConsultantVo {

    @ApiModelProperty(value = "销售顾问名称")
    private String realName;

    @ApiModelProperty(value = "销售顾问ID")
    private String account;

    @ApiModelProperty(value = "店代码")
    private String dealerCode;

    public SalesConsultantVo() {
    }

    public SalesConsultantVo(String realName, String account, String dealerCode) {
        this.realName = realName;
        this.account = account;
        this.dealerCode = dealerCode;
    }
}

