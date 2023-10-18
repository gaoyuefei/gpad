package com.gpad.gpadtool.repository;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.domain.dto.FileInfoDto;
import com.gpad.gpadtool.domain.entity.FileInfo;
import com.gpad.gpadtool.mapper.FileInfoMapper;
import com.gpad.gpadtool.utils.UuidUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName FileInfoRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 17:59:00
 */
@Service
public class FileInfoRepository extends ServiceImpl<FileInfoMapper, FileInfo> {

    public List<FileInfoDto> getByBusinessNoAndLinkType(String businessNo, Integer linkType) {
        LambdaQueryWrapper<FileInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FileInfo::getBusinessNo,businessNo);
        wrapper.eq(FileInfo::getLinkType,linkType);
        List<FileInfo> list = this.list(wrapper);
        List<FileInfoDto> result = new ArrayList<>();
        list.forEach(l->result.add(JSONObject.parseObject(JSONObject.toJSONString(l),FileInfoDto.class)));
        return result;
    }

    public FileInfoDto saveFileInfoDto(FileInfoDto fileInfoDto) {
        fileInfoDto.setId(UuidUtil.generateUuid());
        this.save(JSONObject.parseObject(JSONObject.toJSONString(fileInfoDto),FileInfo.class));
        return fileInfoDto;
    }

    public void saveFileInfoDtoList(List<FileInfoDto> list) {
        List<FileInfo> fileInfos = new ArrayList<>();
        list.forEach(l->fileInfos.add(JSONObject.parseObject(JSONObject.toJSONString(l),FileInfo.class)));
        this.saveBatch(fileInfos);
    }

    public void batchSaveOrUpdateFileInfoDtoList(List<FileInfoDto> list) {
        list.forEach(this::saveOrUpdateFileInfoDto);
    }

    public void saveOrUpdateFileInfoDto(FileInfoDto fileInfoDto) {
        /*if (byBusinessNo==null){
            saveFileInfoDto(fileInfoDto);
        }else {
            updateById(fileInfoDto);
        }*/
    }

    public FileInfoDto updateById(FileInfoDto fileInfoDto){
        this.updateById(JSONObject.parseObject(JSONObject.toJSONString(fileInfoDto),FileInfo.class));
        return fileInfoDto;
    }
}
