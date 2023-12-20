package com.gpad.gpadtool.domain.dto.wxapi.outBo;

import com.gpad.gpadtool.domain.vo.ExhibitionMixPadVo;
import com.gpad.gpadtool.domain.vo.GoodsIdAndNewVIdVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: gpad
 * @BelongsPackage: com.gpad.gpadtool.domain.dto.wxapi.outBo
 * @Author: LF
 * @CreateTime: 2023-12-20
 * @Description: 新商品ID
 * @Version: 1.0
 */
@Data
public class GoodsIdAndNewVIdOutBO {

    @ApiModelProperty(value =  "返回码")
    private String code;

    private List<GoodsIdAndNewVIdVo> data;

    @ApiModelProperty(value =  "异常信息")
    private String msg;

}

