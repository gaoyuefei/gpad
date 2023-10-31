package com.gpad.common.core.bo.input;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @BelongsProject: gpad-api
 * @BelongsPackage: com.gpad.common.core.bo.input
 * @Author: LF
 * @CreateTime: 2023-09-21
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class AuthUserSignatureInputBO {

    @ApiModelProperty(value = "客户签名坐标")
    private String id;

    @ApiModelProperty(value = "订单编号")
    private String bussinessNo;

    @ApiModelProperty(value = "全名")
    private String fullName;


    @ApiModelProperty(value = ":身份类型:1身份证,2护照,3台胞证,4港澳居民来往内地通行证,11营业执照,12统一社会信用代码")
    private Integer identityType;


    @ApiModelProperty(value = "身份证号")
    private String identityCard;

    @ApiModelProperty(value = "电话号码")
    private String mobile;

    @NotBlank
    @ApiModelProperty(value = "全名")
    private String productName;

    @NotBlank
    @ApiModelProperty(value = ":身份类型:1身份证,2护照,3台胞证,4港澳居民来往内地通行证,11营业执照,12统一社会信用代码")
    private Integer identityType1;

    @NotBlank
    @ApiModelProperty(value = "身份证号")
    private String identityCard1;

    @NotBlank
    @ApiModelProperty(value = "电话号码")
    private String productMobile;

    @ApiModelProperty(value = "记忆签")
    private String memorySignPath;


}