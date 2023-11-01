package com.gpad.gpadtool.repository;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.domain.dto.FileInfoDto;
import com.gpad.gpadtool.domain.entity.FileInfo;
import com.gpad.gpadtool.mapper.FileInfoMapper;
import com.gpad.gpadtool.utils.UuidUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName FileInfoRepository.java
 * @Description TODO
 * @createTime 2023年09月22日 17:59:00
 */
@Service
public class FileInfoRepository extends ServiceImpl<FileInfoMapper, FileInfo> {

    public List<FileInfoDto> getBybussinessNoAndLinkType(String bussinessNo, Integer linkType) {
        LambdaQueryWrapper<FileInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FileInfo::getBussinessNo,bussinessNo);
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

    public Boolean saveFileInfoDtoList(List<FileInfoDto> list) {
        List<FileInfo> fileInfos = new ArrayList<>();
        if (CollectionUtil.isEmpty(list) || list.size() == 0){
            return true;
        }
        list.forEach(l->fileInfos.add(JSONObject.parseObject(JSONObject.toJSONString(l),FileInfo.class)));
        boolean b = this.saveBatch(fileInfos);
        return b;
    }

    public void batchSaveOrUpdateFileInfoDtoList(List<FileInfoDto> list) {
        list.forEach(this::saveOrUpdateFileInfoDto);
    }

    public void saveOrUpdateFileInfoDto(FileInfoDto fileInfoDto) {
        /*if (bybussinessNo==null){
            saveFileInfoDto(fileInfoDto);
        }else {
            updateById(fileInfoDto);
        }*/
    }

    public FileInfoDto updateById(FileInfoDto fileInfoDto){
        this.updateById(JSONObject.parseObject(JSONObject.toJSONString(fileInfoDto),FileInfo.class));
        return fileInfoDto;
    }

    public Boolean saveReadyDeliverCarFile(List<FileInfoDto> linkType) {
        return saveFileInfoDtoList(linkType);
    }

    public FileInfo checkSignPath(String bussinessNo, String linkType, String fileType) {
        return this.lambdaQuery().eq(FileInfo::getBussinessNo, bussinessNo)
                .eq(FileInfo::getLinkType, 32)
                .eq(FileInfo::getFileType, 1).one();
    }

    public List<FileInfo> queryFileBybussinessNo(String bussinessNo) {
        return this.lambdaQuery().eq(FileInfo::getBussinessNo,bussinessNo).list();
    }

    public List<FileInfo> getDeliveryCeremonyPath(String bussinessNo, String fileType,String linkType) {
        return this.lambdaQuery()
                .eq(FileInfo::getBussinessNo,bussinessNo)
                .eq(!StringUtils.isBlank(fileType),FileInfo::getFileType,fileType)
                .eq(!StringUtils.isBlank(linkType),FileInfo::getLinkType,linkType)
                .list();
    }

    public Boolean updateReadyDeliverCarFile(List<FileInfoDto> linkType) {
            Boolean result = false;
        if (CollectionUtil.isEmpty(linkType) || linkType.size() == 0){
            return true;
        }
        for (FileInfoDto fileInfoDto : linkType) {
            if (ObjectUtil.isNotEmpty(fileInfoDto)){
                if (!StringUtils.isBlank(fileInfoDto.getId())){
                    result = this.updateFileInfo(fileInfoDto);
                }else {
                    result = this.saveReadyDeliverCarFile(linkType);
                }
            }
        }

        return result;
    }

    private Boolean updateFileInfo(FileInfoDto fileInfoDto) {
        return this.updateById(JSONObject.parseObject(JSONObject.toJSONString(fileInfoDto),FileInfo.class));
    }
}
