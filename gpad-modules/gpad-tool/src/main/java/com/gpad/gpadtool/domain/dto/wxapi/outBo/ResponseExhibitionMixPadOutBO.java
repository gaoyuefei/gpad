package com.gpad.gpadtool.domain.dto.wxapi.outBo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseExhibitionMixPadOutBO {
    /**
     * 返回码
     */
    private String code;
    /**
     * 查列表时：列表数据；查详情时：详情数据；更新时：修改的数量；新增时：新增的主键ID；删除时：删除的数量
     */
    private NewCarAndVersionModel[] data;
    /**
     * 返回说明
     */
    private String msg;


}