package com.gpad.gpadtool.domain.dto.wxapi.outBo;

import com.gpad.gpadtool.domain.vo.WxApiGetCommentVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.dto.wxapi.outBo
 * @Author: LF
 * @CreateTime: 2023-11-09
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class WxApiCommentOutBO {

    @ApiModelProperty(value =  "返回码")
    private String code;

    private WxApiGetCommentVo data;

    @ApiModelProperty(value =  "订单号")
    private String msg;

    @ApiModelProperty(value =  "订单号")
    private Boolean success;
}

