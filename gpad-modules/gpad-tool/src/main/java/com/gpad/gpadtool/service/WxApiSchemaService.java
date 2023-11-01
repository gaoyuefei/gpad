package com.gpad.gpadtool.service;

import com.gpad.common.core.domain.R;

public interface WxApiSchemaService {

    R<String> getgetSkipSchemaUrl(String wxApiSchemaUrl);

    R sitUrlSchema(String wxApiSchemaUrl);

}
