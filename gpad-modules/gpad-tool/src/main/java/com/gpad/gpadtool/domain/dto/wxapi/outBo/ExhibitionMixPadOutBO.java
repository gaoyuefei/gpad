package com.gpad.gpadtool.domain.dto.wxapi.outBo;

import com.gpad.gpadtool.domain.vo.ExhibitionMixPadVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.dto.wxapi.outBo
 * @Author: LF
 * @CreateTime: 2023-11-10
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class ExhibitionMixPadOutBO {

    @ApiModelProperty(value =  "返回码")
    private String code;

    private List<ExhibitionMixPadVo> data;

    @ApiModelProperty(value =  "订单号")
    private String msg;

}

