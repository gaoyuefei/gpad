package com.gpad.gpadtool.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpad.gpadtool.domain.dto.scrm.ScrmUser;
import com.gpad.gpadtool.mapper.ScrmUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmUserRepository.java
 * @Description TODO
 * @createTime 2023年09月21日 16:54:00
 */
@Slf4j
@Service
public class ScrmUserRepository extends ServiceImpl<ScrmUserMapper, ScrmUser> {
}
