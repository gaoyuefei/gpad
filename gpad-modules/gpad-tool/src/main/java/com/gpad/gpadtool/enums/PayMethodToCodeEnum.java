package com.gpad.gpadtool.enums;

import com.gpad.common.core.utils.StringUtils;

/**
 * SCRM  1：PDF  2：图片  3：视频
 * @ClassName GrtToPadEnums
 * @Date: 2022/9/21 9:53
 * @Version: 1.0
 * @Author: LF
 */
public enum PayMethodToCodeEnum {

    /**
     * 默认
     */
    DEFAULT_PAY_METHOD("0","默认"),

    /**
     * 一次性付清
     */
    PAY_IN_FULL("1","一次性付清"),

    /**
     * 按揭付款
     */
    MORTGAGE_PAYMENT("2","按揭付款"),

    /**
     * 状态一
     */
    HIRE_PURCHASE("3","分期付款"),
    /**
     * 其他付款
     */
    OTHER_PAY_METHOD("4","其他"),
    /**
     * 无
     */
    NOT_PAY_METHOD("5","无"),
    ;
    private String payCode;

    private String payDesc;

    PayMethodToCodeEnum(String payCode, String payDesc) {
        this.payCode = payCode;
        this.payDesc = payDesc;
    }

    public String getPayCode() {
        return payCode;
    }

    public String getPayDesc() {
        return payDesc;
    }

    public static String getPadValueByType(String type) {
        PayMethodToCodeEnum[] values = PayMethodToCodeEnum.values();
        for (PayMethodToCodeEnum value : values) {
            if(type != null && type.equals(value.getPayDesc())){
                return value.getPayCode();
            }
        }
        return "5";
    }
}

