package com.gpad.gpadtool.repository;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.common.core.bo.input.AutoSignatureInputBO;
import com.gpad.gpadtool.domain.entity.GpadIdentityAuthInfo;
import com.gpad.gpadtool.mapper.GpadIdentityAuthInfoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName FileInfoRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 17:59:00
 */
@Service
public class GpadIdentityAuthInfoRepository extends ServiceImpl<GpadIdentityAuthInfoMapper, GpadIdentityAuthInfo> {


    public GpadIdentityAuthInfo checkMemorySign(AutoSignatureInputBO autoSignatureInputBO) {
        List<GpadIdentityAuthInfo> list = this.lambdaQuery().eq(GpadIdentityAuthInfo::getBussinessNo, autoSignatureInputBO.getBussinessNo()).list();
        if (CollectionUtil.isNotEmpty(list) && list.size() > 0){
            return list.get(0);
        }
        return new GpadIdentityAuthInfo();
    }
}
