package com.gpad.gpadtool.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.domain.entity.GpadDlrAuthenticationEmailInfo;
import com.gpad.gpadtool.mapper.GpadDlrAuthenticationEmailInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName FileInfoRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 17:59:00
 */
@Slf4j
@Service
public class GpadDlrAuthenticationEmailInfoRepository extends ServiceImpl<GpadDlrAuthenticationEmailInfoMapper, GpadDlrAuthenticationEmailInfo> {

    public GpadDlrAuthenticationEmailInfo queryEmail(String dealerCode) {
        return this.lambdaQuery().eq(GpadDlrAuthenticationEmailInfo::getDealerCode, dealerCode).eq(GpadDlrAuthenticationEmailInfo::getDelFlag, 0).one();
    }
}
