package com.gpad.gpadtool.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.domain.dto.HandoverCarPrepareDto;
import com.gpad.gpadtool.domain.entity.HandoverCarPrepare;
import com.gpad.gpadtool.mapper.HandoverCarPrepareMapper;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName HandoverCarPrepareRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 17:18:00
 */
@Service
public class HandoverCarPrepareRepository extends ServiceImpl<HandoverCarPrepareMapper, HandoverCarPrepare> {

    public Boolean saveReadyDeliverCarInfoOrderNo(HandoverCarPrepareDto handoverCarPrepareDto) {
        HandoverCarPrepare handoverCarPrepare = HandoverCarPrepare.builder()
                .bussinessNo(handoverCarPrepareDto.getBussinessNo())
                .loanStatus(handoverCarPrepareDto.getLoanStatus())
                .purchaseTax(handoverCarPrepareDto.getPurchaseTax())
                .unifiedSalesInvoice(handoverCarPrepareDto.getUnifiedSalesInvoice())
                .carLicense(handoverCarPrepareDto.getCarLicense())
                .licensePlateNum(handoverCarPrepareDto.getLicensePlateNum())
                .carCertificate(handoverCarPrepareDto.getCarCertificate())
                .supplies(handoverCarPrepareDto.getSupplies()+"")
                .remark(handoverCarPrepareDto.getRemark())
                .build();

        return this.save(handoverCarPrepare);
    }

    public HandoverCarPrepare queryBybussinessNo(String bussinessNo) {
        List<HandoverCarPrepare> list = this.lambdaQuery().eq(HandoverCarPrepare::getBussinessNo, bussinessNo).list();
        return CollectionUtil.isEmpty(list)?(new HandoverCarPrepare()):(list.get(0));
    }

    public Boolean checkedbussinessNo(String bussinessNo) {
        List<HandoverCarPrepare> list = this.lambdaQuery().eq(HandoverCarPrepare::getBussinessNo, bussinessNo).list();
        if (CollectionUtil.isEmpty(list)){
            return false;
        }
        return list.size() > 0;
    }

    public void updateReadyDeliverCarInfoOrderNo(HandoverCarPrepareDto handoverCarPrepareDto) {
        HandoverCarPrepare handoverCarPrepare = new HandoverCarPrepare();
        BeanUtil.copyProperties(handoverCarPrepareDto,handoverCarPrepare);
        this.saveOrUpdate(handoverCarPrepare);
    }
}
