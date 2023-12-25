package com.gpad.gpadtool.service;

import com.gpad.common.core.domain.R;
import com.gpad.gpadtool.domain.dto.CommonFilePathCheckInputBO;
import com.gpad.gpadtool.domain.dto.FileInfoDto;
import com.gpad.gpadtool.domain.entity.FileInfo;
import com.gpad.gpadtool.repository.FileInfoRepository;
import com.gpad.gpadtool.repository.OrderNoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName OrderNoService.java
 * @Description TODO
 * @createTime 2023年09月22日 15:18:00
 */
@Service
public class FileInfoService {

    @Autowired
    private FileInfoRepository fileInfoRepository;


    public R<List<FileInfoDto>> queryCommonFile(CommonFilePathCheckInputBO commonFilePathCheckInputBO) {
        return  fileInfoRepository.queryCommonFile(commonFilePathCheckInputBO);
    }

    public String selectSysConfigByKey(String sysKey){
        return fileInfoRepository.selectSysConfigByKey(sysKey);
    }

    public List<FileInfoDto> getByBusinessNoAndLinkType(String businessNo, Integer linkType){
        return fileInfoRepository.getBybussinessNoAndLinkType(businessNo,linkType);
    }
}
