package com.gpad.gpadtool.service;

import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.wxapi.ExhibitionMixPadInputBO;
import com.gpad.gpadtool.domain.dto.wxapi.WxApiCommentInputBO;
import com.gpad.gpadtool.domain.vo.OrderCommentUrlVo;

public interface WxApiSchemaService {

    R<String> getgetSkipSchemaUrl(String wxApiSchemaUrl);

    R sitUrlSchema(String wxApiSchemaUrl);

    R getOrderComment(WxApiCommentInputBO wxApiCommentInputBO);

    R queryExhibitionMixPad(ExhibitionMixPadInputBO exhibitionMixPadInputBO);

    R getOrderCommentUrl(OrderCommentUrlVo orderCommentUrlVo);

}
