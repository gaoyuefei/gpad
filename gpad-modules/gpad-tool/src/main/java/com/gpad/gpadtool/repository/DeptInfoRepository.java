package com.gpad.gpadtool.repository;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.constant.StatusCode;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.gpadtool.constant.SnowIdGenerator;
import com.gpad.gpadtool.domain.dto.DeptInfoDto;
import com.gpad.gpadtool.domain.entity.DeptInfo;
import com.gpad.gpadtool.mapper.DeptInfoMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName DeptInfoRepository.java
 * @Description TODO
 * @createTime 2023年09月25日 10:00:00
 */
@Service
public class DeptInfoRepository extends ServiceImpl<DeptInfoMapper, DeptInfo> {

    
    public List<DeptInfoDto> listAllDepts() {
        List<DeptInfoDto> result = new ArrayList<>();
        List<DeptInfo> list = this.list();
        list.forEach(l->result.add(JSONObject.parseObject(JSONObject.toJSONString(l),DeptInfoDto.class)));
        return result;
    }

    
    public DeptInfoDto findById(Long id) {
        DeptInfo byId = this.getById(id);
        return JSONObject.parseObject(JSONObject.toJSONString(byId),DeptInfoDto.class);
    }

    
    public DeptInfoDto add(DeptInfoDto deptInfoDto) {
        deptInfoDto.setId(new SnowIdGenerator().nextId());
        deptInfoDto.setCreateTime(new Date());
        this.save(JSONObject.parseObject(JSONObject.toJSONString(deptInfoDto),DeptInfo.class));
        return deptInfoDto;
    }

    
    public void addBatchDepts(List<DeptInfoDto> deptInfoDtos){
        List<DeptInfo> deptInfos = new ArrayList<>();
        deptInfoDtos.forEach(u->{
//            u.setId(new SnowIdGenerator().nextId());
            deptInfos.add(JSONObject.parseObject(JSONObject.toJSONString(u),DeptInfo.class));
        });
        this.saveOrUpdateBatch(deptInfos);
    }

    
    public DeptInfoDto updateById(DeptInfoDto deptInfoDto) {
        Long id = deptInfoDto.getId();
        DeptInfo byId = this.getById(id);
        if (byId == null){
            throw new ServiceException(StatusCode.NO_DATA.getDesc());
        }
        deptInfoDto.setUpdateTime(new Date());
        this.updateById(JSONObject.parseObject(JSONObject.toJSONString(deptInfoDto),DeptInfo.class));
        return deptInfoDto;
    }

    
    public void delete(DeptInfoDto deptInfoDto) {
        this.deleteById(deptInfoDto.getId());
    }

    
    public void deleteById(Long id) {
        this.removeById(id);
    }
}
