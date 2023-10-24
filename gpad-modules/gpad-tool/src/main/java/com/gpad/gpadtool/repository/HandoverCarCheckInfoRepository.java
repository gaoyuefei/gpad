package com.gpad.gpadtool.repository;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.common.core.bo.input.AutoSignatureInputBO;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.gpadtool.domain.dto.HandoverCarCheckInfoDto;
import com.gpad.gpadtool.domain.entity.HandoverCarCheckInfo;
import com.gpad.gpadtool.mapper.HandoverCarCheckInfoMapper;
import org.springframework.stereotype.Service;


/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName HandoverCarCheckInfoRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 17:29:00
 */
@Service
public class HandoverCarCheckInfoRepository extends ServiceImpl<HandoverCarCheckInfoMapper, HandoverCarCheckInfo> {


    public HandoverCarCheckInfo queryDeliverCarConfirmInfo(String bussinessNo) {
        HandoverCarCheckInfo handoverCarCheckInfo = this.lambdaQuery().eq(HandoverCarCheckInfo::getBussinessNo, bussinessNo)
                .eq(HandoverCarCheckInfo::getDelFlag, 0)
                .one();
        return handoverCarCheckInfo;
    }

    public Boolean saveDeliverCarConfirmInfo(HandoverCarCheckInfoDto handoverCarCheckInfoDto) {
        // XXX
        HandoverCarCheckInfo handoverCarCheckInfo = new HandoverCarCheckInfo();
        BeanUtil.copyProperties(handoverCarCheckInfoDto,handoverCarCheckInfo);
        return this.save(handoverCarCheckInfo);
    }

    public void getAplNoByBussinessNo(String bussinessNo) {
//        this.lambdaQuery().eq()
    }

    public Boolean updatecontractInfoById(String apl, AutoSignatureInputBO autoSignatureInputBO) {
        boolean update = this.lambdaUpdate().setSql(" version = version + 1 ")
                .set(HandoverCarCheckInfo::getContractAplNo, apl)
                .eq(HandoverCarCheckInfo::getDelFlag, "0")
                .eq(HandoverCarCheckInfo::getBussinessNo, autoSignatureInputBO.getBussinessNo())
                .eq(StringUtils.isNotEmpty(autoSignatureInputBO.getId()), HandoverCarCheckInfo::getId, autoSignatureInputBO.getId())
                .update();
        return update;

    }
}
