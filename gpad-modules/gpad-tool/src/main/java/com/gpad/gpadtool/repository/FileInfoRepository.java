package com.gpad.gpadtool.repository;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.CommonFilePathCheckInputBO;
import com.gpad.gpadtool.domain.dto.FileInfoDto;
import com.gpad.gpadtool.domain.entity.FileInfo;
import com.gpad.gpadtool.mapper.FileInfoMapper;
import com.gpad.gpadtool.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
@Service
public class FileInfoRepository extends ServiceImpl<FileInfoMapper, FileInfo> {

    public List<FileInfoDto> getBybussinessNoAndLinkType(String bussinessNo, Integer linkType) {
        LambdaQueryWrapper<FileInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FileInfo::getBussinessNo,bussinessNo);
        wrapper.eq(FileInfo::getLinkType,linkType);
        wrapper.eq(FileInfo::getDelFlag,0);
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

    public Boolean saveOrUpdateFileInfoDto(FileInfoDto fileInfoDto) {
        return this.saveOrUpdate(JSONObject.parseObject(JSONObject.toJSONString(fileInfoDto),FileInfo.class));
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
        return this.lambdaQuery().eq(FileInfo::getBussinessNo,bussinessNo).eq(FileInfo::getDelFlag,0).list();
    }

    public List<FileInfo> getDeliveryCeremonyPath(String bussinessNo, String fileType,String linkType) {
        return this.lambdaQuery()
                .eq(FileInfo::getBussinessNo,bussinessNo)
                .eq(!StringUtils.isBlank(fileType),FileInfo::getFileType,fileType)
                .eq(!StringUtils.isBlank(linkType),FileInfo::getLinkType,linkType)
                .eq(FileInfo::getDelFlag,0)
                .list();
    }

    public Boolean updateReadyDeliverCarFile(List<FileInfoDto> linkType,String bussinessNo) {
        Boolean result = false;
        result = this.queryFileIsExist(bussinessNo);
        log.info("查询订单号文件状态为{}，true 查询为空， false查询到了数据，",result);
        if (!result){
            result = this.delFileInfoAll(bussinessNo);
        }
        log.info("逻辑删除结束状态为{}",result);
        if (CollectionUtil.isEmpty(linkType) || linkType.size() == 0){
            return true;
        }
        for (FileInfoDto fileInfoDto : linkType) {
            if (ObjectUtil.isNotEmpty(fileInfoDto)){
                if (!StringUtils.isBlank(fileInfoDto.getId())){
                    result = this.updateFileInfo(fileInfoDto);
                }else {
                    result = this.saveReadyDeliverCarFileBnNO(fileInfoDto);
                }
            }
        }
        return result;
    }

    private Boolean queryFileIsExist(String bussinessNo) {
        List<FileInfo> list = this.lambdaQuery().eq(FileInfo::getBussinessNo, bussinessNo)
                .eq(FileInfo::getDelFlag, 0).list();
        if (CollectionUtil.isNotEmpty(list)){
            boolean b = list.size() > 0;
            return !b;
        }
        return true;
    }

    public Boolean delFileInfoAll(String bussinessNo) {
        return this.lambdaUpdate().setSql(" version = version + 1 ")
                .set(FileInfo::getDelFlag,1)
                .eq(FileInfo::getFileType,"21")
                .or()
                .eq(FileInfo::getFileType,"22")
                .eq(FileInfo::getBussinessNo,bussinessNo)
                .eq(FileInfo::getDelFlag,0).update();
    }

    public Boolean delFileInfo(FileInfoDto fileInfoDto) {
        FileInfo fileInfo = new FileInfo();
        BeanUtil.copyProperties(fileInfoDto,fileInfo);
        fileInfo.setDelFlag(1);
        return saveOrUpdate(fileInfo);
    }

    private Boolean saveReadyDeliverCarFileBnNO(FileInfoDto fileInfoDto) {
            FileInfo fileInfo = new FileInfo();
            BeanUtil.copyProperties(fileInfoDto,fileInfo);
            fileInfo.setDelFlag(0);
            saveOrUpdate(fileInfo);
        return true;
    }

    private Boolean updateFileInfo(FileInfoDto fileInfoDto) {
        FileInfo entity = JSONObject.parseObject(JSONObject.toJSONString(fileInfoDto), FileInfo.class);
        entity.setDelFlag(0);
        log.info("method:saveReadyDeliverCarFileNo().交车准备内容: {}", JSONObject.toJSONString(entity));
        return this.updateById(entity);
    }

    public Boolean saveReadyDeliverCarFileNo(List<FileInfoDto> list, String bussinessNo) {
        List<FileInfo> fileInfos = new ArrayList<>();
        if (CollectionUtil.isEmpty(list) || list.size() == 0){
            return true;
        }
        list.forEach(l->fileInfos.add(JSONObject.parseObject(JSONObject.toJSONString(l),FileInfo.class)));
        fileInfos.forEach(s -> s.setBussinessNo(bussinessNo));
        log.info("method:saveReadyDeliverCarFileNo().交车准备内容: {}", fileInfos);
        return this.saveBatch(fileInfos);
    }

    public R<List<FileInfoDto>> queryCommonFile(CommonFilePathCheckInputBO commonFilePathCheckInputBO) {
        log.info("进入方法 mehtod：queryCommonFile(){}", JSON.toJSONString(commonFilePathCheckInputBO));
        List<FileInfo> list = this.lambdaQuery().eq(FileInfo::getBussinessNo, commonFilePathCheckInputBO.getBussinessNo())
                .eq(StringUtils.isNotBlank(commonFilePathCheckInputBO.getLinkType()),FileInfo::getLinkType, commonFilePathCheckInputBO.getLinkType())
                .eq(StringUtils.isNotBlank(commonFilePathCheckInputBO.getFileType()),FileInfo::getFileType, commonFilePathCheckInputBO.getFileType())
                .list();
        log.info("方式执行结束 mehtod：list(){}", JSON.toJSONString(list));

        List<FileInfoDto> fileInfoOutBo = new ArrayList<>();
        list.forEach(fileInfo -> {
            FileInfoDto fileInfoDto = new FileInfoDto();
            BeanUtils.copyProperties(fileInfo,fileInfoDto);
            fileInfoOutBo.add(fileInfoDto);
        });
        log.info("方式执行结束 对象复制之后mehtod：queryCommonFile(){}", JSON.toJSONString(fileInfoOutBo));
        return R.ok(fileInfoOutBo);
    }

    @Autowired
    private FileInfoMapper fileInfoMapper;

    public String selectSysConfigByKey(String sysKey){
        return fileInfoMapper.selectSysConfigByKey(sysKey);
    }
}
