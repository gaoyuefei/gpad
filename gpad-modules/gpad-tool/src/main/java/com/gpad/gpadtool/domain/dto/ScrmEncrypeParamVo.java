package com.gpad.gpadtool.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName ScrmEncrypeParamVo.java
 * @Description TODO
 * @createTime 2023年09月22日 10:28:00
 */
@Data
public class ScrmEncrypeParamVo {
    @NotBlank
    String data;
}
