package com.gpad.gpadtool.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gpad.gpadtool.domain.dto.scrm.ScrmUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmUserMapper.java
 * @Description TODO
 * @createTime 2023年09月21日 16:50:00
 */
@Mapper
public interface ScrmUserMapper extends BaseMapper<ScrmUser> {
}
