package com.gpad.gpadtool.service;

import com.gpad.common.core.bo.input.AutoSignatureGetLinkInputBO;
import com.gpad.common.core.bo.input.AutoSignatureInputBO;
import com.gpad.common.core.bo.input.ContinueStartSignatureInputBO;
import com.gpad.common.core.bo.input.IdentityAuthInputBO;
import com.gpad.common.core.domain.R;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmServiceImpl.java
 * @Description TODO
 * @createTime 2023年09月21日 11:18:00
 */
public interface AutoSignatureService {


    R<String> startGentlemanSignature(AutoSignatureInputBO autoSignatureInputBO, MultipartFile file,MultipartFile fileCustomerPng,MultipartFile fileProductPng);

    R<String> getStartGentlemanSignatureLink(AutoSignatureGetLinkInputBO autoSignatureGetLinkInputBO);

    R continueStartGenManSignature(ContinueStartSignatureInputBO continueStartSignatureInputBO);

    R authUserValid(AutoSignatureInputBO autoSignatureInputBO);

    R turnOffSignature(String status, String bussinessNo);
}
