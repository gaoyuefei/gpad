package com.gpad.common.core.bo.outBo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @BelongsProject: gpad-api
 * @BelongsPackage: com.gpad.common.core.bo.outBo
 * @Author: LF
 * @CreateTime: 2023-09-21
 * @Description: TODO
 * @Version: 1.0
 */
public class AutoSignatureOutBO {

    @ApiModelProperty(value="签名坐标")
    private String chapteJson;

    @ApiModelProperty(value="合同名称")
    private String contractName;

    @ApiModelProperty(value="全名")
    private String fullName;

    @ApiModelProperty(value=":身份类型:1身份证,2护照,3台胞证,4港澳居民来往内地通行证,11营业执照,12统一社会信用代码")
    private Integer identityType;

    @ApiModelProperty(value="身份证号")
    private String identityCard;

    @ApiModelProperty(value="电话号码")
    private String mobile;
}

