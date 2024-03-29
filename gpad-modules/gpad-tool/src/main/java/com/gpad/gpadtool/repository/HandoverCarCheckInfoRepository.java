package com.gpad.gpadtool.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.common.core.bo.input.AutoSignatureInputBO;
import com.gpad.common.core.utils.StringUtils;
import com.gpad.gpadtool.domain.dto.HandoverCarCheckInfoDto;
import com.gpad.gpadtool.domain.entity.HandoverCarCheckInfo;
import com.gpad.gpadtool.mapper.HandoverCarCheckInfoMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * @author Donald.Lee
 * @version 1.0.1
 * @ClassName HandoverCarCheckInfoRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 17:29:00
 */
@Service
public class HandoverCarCheckInfoRepository extends ServiceImpl<HandoverCarCheckInfoMapper, HandoverCarCheckInfo> {


    public HandoverCarCheckInfo queryDeliverCarConfirmInfo(String bussinessNo) {
        List<HandoverCarCheckInfo> list = this.lambdaQuery().eq(HandoverCarCheckInfo::getBussinessNo, bussinessNo)
                .orderByDesc(HandoverCarCheckInfo::getCreateTime)
                .eq(HandoverCarCheckInfo::getDelFlag, 0).list();
        if(!CollectionUtil.isEmpty(list)){
         return list.size() > 0 ? (list.get(0)):(new HandoverCarCheckInfo());
        }
        return new HandoverCarCheckInfo();
    }

    public Boolean saveDeliverCarConfirmInfo(HandoverCarCheckInfoDto handoverCarCheckInfoDto) {
        // XXX
        HandoverCarCheckInfo handoverCarCheckInfo = new HandoverCarCheckInfo();
        BeanUtil.copyProperties(handoverCarCheckInfoDto,handoverCarCheckInfo);
        handoverCarCheckInfo.setId(handoverCarCheckInfoDto.getId());
        if (ObjectUtil.isNotEmpty(handoverCarCheckInfoDto)){
            Long id = handoverCarCheckInfo.getId();
            if ((null == id || 0 == id)){
                    String str = id + "";
                    handoverCarCheckInfo.setId(str.length()<=9?null:id);
                    //标记
                    str = null;
            }
        }
        return this.saveOrUpdate(handoverCarCheckInfo);
    }

    public void getAplNoByBussinessNo(String bussinessNo) {
//        this.lambdaQuery().eq()
    }

    public Boolean updatecontractInfoById(String apl, String data ,AutoSignatureInputBO autoSignatureInputBO) {
        boolean update = this.lambdaUpdate().setSql(" version = version + 1 ")
                .set(StringUtils.isNotEmpty(apl) && apl.length() < 50 ,HandoverCarCheckInfo::getContractAplNo, apl)
                .set(HandoverCarCheckInfo::getSignType, 0)
                .set(HandoverCarCheckInfo::getSignStatus, 1)
                .set(StringUtils.isNotEmpty(data),HandoverCarCheckInfo::getContractLink,data)
                .set(HandoverCarCheckInfo::getUpdateTime,new Date())
                .eq(HandoverCarCheckInfo::getDelFlag, "0")
                .eq(HandoverCarCheckInfo::getBussinessNo, autoSignatureInputBO.getBussinessNo())
                .eq(StringUtils.isNotEmpty(autoSignatureInputBO.getId()), HandoverCarCheckInfo::getId, autoSignatureInputBO.getId())
                .update();
        return update;

    }

    public Boolean updateSignTypeById(Long id,String bussinessNo) {
        boolean update = this.lambdaUpdate().setSql(" version = version + 1 ")
                .set(HandoverCarCheckInfo::getSignType, 1)
                .set(HandoverCarCheckInfo::getSignStatus, 1)
                .eq(HandoverCarCheckInfo::getDelFlag, "0")
                .eq(HandoverCarCheckInfo::getBussinessNo, bussinessNo)
                .eq(StringUtils.isNotEmpty(id+""), HandoverCarCheckInfo::getId,id)
                .update();
        return update;
    }

    public boolean saveOrUpdateHandoverCarInfo(HandoverCarCheckInfo handoverCarInfor) {
        return this.saveOrUpdate(handoverCarInfor);
    }
}
