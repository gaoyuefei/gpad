package com.gpad.gpadtool.repository;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.common.core.constant.StatusCode;
import com.gpad.common.core.exception.ServiceException;
import com.gpad.gpadtool.constant.SnowIdGenerator;
import com.gpad.gpadtool.domain.dto.UserInfoDto;
import com.gpad.gpadtool.domain.entity.UserInfo;
import com.gpad.gpadtool.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName UserInfoRepository.java
 * @Description TODO
 * @createTime 2023年09月25日 11:08:00
 */
@Service
public class UserInfoRepository extends ServiceImpl<UserInfoMapper, UserInfo> {
    
    public List<UserInfoDto> listAllUsers() {
        List<UserInfoDto> result = new ArrayList<>();
        List<UserInfo> list = this.list();
        list.forEach(l->result.add(JSONObject.parseObject(JSONObject.toJSONString(l),UserInfoDto.class)));
        return result;
    }

    
    public UserInfoDto findById(Long id) {
        UserInfo byId = this.getById(id);
        return JSONObject.parseObject(JSONObject.toJSONString(byId),UserInfoDto.class);
    }

    
    public UserInfoDto add(UserInfoDto userInfoDto) {
        userInfoDto.setId(new SnowIdGenerator().nextId());
        userInfoDto.setSyncTime(new Date());
        this.save(JSONObject.parseObject(JSONObject.toJSONString(userInfoDto),UserInfo.class));
        return userInfoDto;
    }

    
    public void addBatchUsers(List<UserInfoDto> userInfoDtos){
        List<UserInfo> userInfos = new ArrayList<>();
        userInfoDtos.forEach(u->{
            u.setId(new SnowIdGenerator().nextId());
            userInfos.add(JSONObject.parseObject(JSONObject.toJSONString(u),UserInfo.class));
        });
        this.saveBatch(userInfos);
    }

    
    public UserInfoDto updateById(UserInfoDto userInfoDto) {
        Long id = userInfoDto.getId();
        UserInfo byId = this.getById(id);
        if (byId == null){
            throw new ServiceException(StatusCode.NO_DATA.getDesc());
        }
        userInfoDto.setUpdateTime(new Date());
        this.updateById(JSONObject.parseObject(JSONObject.toJSONString(userInfoDto),UserInfo.class));
        return userInfoDto;
    }

    
    public void delete(UserInfoDto userInfoDto) {
        this.deleteById(userInfoDto.getId());
    }

    
    public void deleteById(Long id) {
        this.removeById(id);
    }

    
    public void saveOrUpdateByAccount(List<UserInfoDto> userInfos){
        userInfos.forEach(u->{
            UserInfo userInfo = JSONObject.parseObject(JSONObject.toJSONString(u), UserInfo.class);
            UserInfo byAccount = this.findByAccount(u.getAccount());
            if (byAccount == null){
                u.setSyncTime(new Date());
                this.save(userInfo);
            }else {
                u.setUpdateTime(new Date());
                this.updateById(userInfo);
            }
        });
    }

    
    public UserInfo findByAccount(String account){
        LambdaQueryWrapper<UserInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserInfo::getAccount,account);
        return this.getOne(wrapper);
    }

    
    public UserInfoDto findDtoByAccount(String account){
        UserInfo byAccount = this.findByAccount(account);
        return JSONObject.parseObject(JSONObject.toJSONString(byAccount),UserInfoDto.class);
    }
}
