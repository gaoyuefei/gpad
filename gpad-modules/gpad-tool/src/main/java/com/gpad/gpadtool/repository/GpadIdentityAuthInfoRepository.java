package com.gpad.gpadtool.repository;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.common.core.bo.input.AuthUserSignatureInputBO;
import com.gpad.common.core.bo.input.AutoSignatureInputBO;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.gpadtool.domain.entity.GpadIdentityAuthInfo;
import com.gpad.gpadtool.mapper.GpadIdentityAuthInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName FileInfoRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 17:59:00
 */
@Slf4j
@Service
public class GpadIdentityAuthInfoRepository extends ServiceImpl<GpadIdentityAuthInfoMapper, GpadIdentityAuthInfo> {


    public GpadIdentityAuthInfo checkMemorySign(String account) {
        List<GpadIdentityAuthInfo> list = this.lambdaQuery().eq(GpadIdentityAuthInfo::getAccount, account)
                .orderByDesc(GpadIdentityAuthInfo::getCreateTime)
                .list();
        if (CollectionUtil.isNotEmpty(list) && list.size() > 0){
            return list.get(0);
        }
        return GpadIdentityAuthInfo.builder().build();
    }

    public Boolean saveAuthUserSignatureValid(AuthUserSignatureInputBO authUserSignatureInputBO) {
        log.info("method:saveAuthUserSignatureValid{}，保存修改入参{}",authUserSignatureInputBO.getId(), JSON.toJSONString(authUserSignatureInputBO));
        String id = authUserSignatureInputBO.getId();
        // TODO 账号用户名
        GpadIdentityAuthInfo build = GpadIdentityAuthInfo.builder()
                .id(StringUtils.isEmpty(id)?null:Long.valueOf(id))
                .identityCard(authUserSignatureInputBO.getIdentityCard1())
                .productMobile(authUserSignatureInputBO.getProductMobile())
                .productName(authUserSignatureInputBO.getProductName())
                .filePath(authUserSignatureInputBO.getMemorySignPath())
                .identityType("1")
                .account(authUserSignatureInputBO.getAccount())
                .version(0)
                .delFlag(0)
                .createTime(new Date())
                .build();
        log.info("method:saveAuthUserSignatureValid，封装完后参数为build{}", JSON.toJSONString(build));
        boolean b = this.saveOrUpdate(build);
        log.info("method:saveOrUpdate，执行结果b{}", JSON.toJSONString(b));
        return b;
    }

    public Boolean updateAuthUserSignature(AuthUserSignatureInputBO authUserSignatureInputBO) {
        log.info("method:updateAuthUserSignature，封装完后参数为 id为{}", JSON.toJSONString(authUserSignatureInputBO));
        return this.saveAuthUserSignatureValid(authUserSignatureInputBO);
    }

    public GpadIdentityAuthInfo selectByAccount(String account) {
        GpadIdentityAuthInfo gpadIdentityAuthInfo = null;
        List<GpadIdentityAuthInfo> list = this.lambdaQuery().eq(GpadIdentityAuthInfo::getAccount, account)
                .list();
        log.info("查询返回：{}",JSON.toJSONString(list));
        if (CollectionUtil.isNotEmpty(list)){
            if (list.size() > 0){
                gpadIdentityAuthInfo = list.get(0);
            }
        }
        log.info("返回实体为：{}",JSON.toJSONString(gpadIdentityAuthInfo));
        return gpadIdentityAuthInfo;
    }
}
