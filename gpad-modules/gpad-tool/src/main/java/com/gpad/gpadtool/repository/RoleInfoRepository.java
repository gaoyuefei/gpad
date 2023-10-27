package com.gpad.gpadtool.repository;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.constant.StatusCode;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.gpadtool.constant.SnowIdGenerator;
import com.gpad.gpadtool.domain.dto.RoleInfoDto;
import com.gpad.gpadtool.domain.entity.RoleInfo;
import com.gpad.gpadtool.mapper.RoleInfoMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName RoleInfoRepository.java
 * @Description TODO
 * @createTime 2023年09月25日 11:17:00
 */
@Service
public class RoleInfoRepository extends ServiceImpl<RoleInfoMapper, RoleInfo> {

    
    public List<RoleInfoDto> listAllRoles() {
        List<RoleInfoDto> result = new ArrayList<>();
        List<RoleInfo> list = this.list();
        list.forEach(l->result.add(JSONObject.parseObject(JSONObject.toJSONString(l),RoleInfoDto.class)));
        return result;
    }

    
    public RoleInfoDto findById(Long id) {
        RoleInfo byId = this.getById(id);
        return JSONObject.parseObject(JSONObject.toJSONString(byId),RoleInfoDto.class);
    }

    
    public RoleInfoDto add(RoleInfoDto roleInfoDto) {
        roleInfoDto.setId(new SnowIdGenerator().nextId());
        this.save(JSONObject.parseObject(JSONObject.toJSONString(roleInfoDto),RoleInfo.class));
        return roleInfoDto;
    }

    
    public void addBatchRoles(List<RoleInfoDto> roleInfoDtos){
        List<RoleInfo> roleInfos = new ArrayList<>();
        roleInfoDtos.forEach(u->{
//            u.setId(new SnowIdGenerator().nextId());
            roleInfos.add(JSONObject.parseObject(JSONObject.toJSONString(u),RoleInfo.class));
        });
        this.saveOrUpdateBatch(roleInfos);
    }

    
    public RoleInfoDto updateById(RoleInfoDto roleInfoDto) {
        Long id = roleInfoDto.getId();
        RoleInfo byId = this.getById(id);
        if (byId == null){
            throw new ServiceException(StatusCode.NO_DATA.getDesc());
        }
        this.updateById(JSONObject.parseObject(JSONObject.toJSONString(roleInfoDto),RoleInfo.class));
        return roleInfoDto;
    }

    
    public void delete(RoleInfoDto roleInfoDto) {
        this.deleteById(roleInfoDto.getId());
    }

    
    public void deleteById(Long id) {
        this.removeById(id);
    }
}
