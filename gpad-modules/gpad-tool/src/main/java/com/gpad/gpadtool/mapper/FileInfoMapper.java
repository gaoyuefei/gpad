package com.gpad.gpadtool.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.gpad.gpadtool.domain.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    @Select("select config_value from sys_config where config_key = #{configKey}")
    String selectSysConfigByKey(@Param("configKey")String configKey);


    Boolean updateDelFlagByOrderNo(@Param("bussinessNo") String bussinessNo);
}
