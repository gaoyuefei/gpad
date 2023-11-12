package com.gpad.gpadtool.enums;

import com.gpad.common.core.utils.StringUtils;

/**
 * SCRM  1：PDF  2：图片  3：视频
 * @ClassName GrtToPadEnums
 * @Date: 2022/9/21 9:53
 * @Version: 1.0
 * @Author: LF
 */
public enum WxToPadSchemaUrlTypeEnum {

    /**
     * 未配车
     */
    DEFAULT_ORDER_TYPE_ZERO("0", "7","线上订单"),

    /**
     * 状态一
     */
    DEFAULT_ORDER_TYPE_ONE("1", "1","线上订单"),
    /**
     * 状态二
     */
    DEFAULT_ORDER_TYPE_TWO("1", "2","线上订单"),
    /**
     * 状态三
     */
    DEFAULT_ORDER_TYPE_THREE("1", "3","线上订单"),
    /**
     * 状态四
     */
    DEFAULT_ORDER_TYPE_FOUR("1", "4","线上订单"),
    /**
     * 状态五
     */
    DEFAULT_ORDER_TYPE_FIVE("1", "5","线上订单"),
    /**
     * 状态六
     */
    DEFAULT_ORDER_TYPE_SIX("1", "6","线上订单"),


    DEFAULT_ORDER_VIDEO_TEN("2", "10","线下订单"),
    ;
    private String skipUrlTypeValue;

    private String wxValue;

    private String desc;

    WxToPadSchemaUrlTypeEnum(String skipUrlTypeValue, String wxValue, String desc) {
        this.skipUrlTypeValue = skipUrlTypeValue;
        this.wxValue = wxValue;
        this.desc = desc;
    }

    public String getSkipUrlTypeValue() {
        return skipUrlTypeValue;
    }

    public void setSkipUrlTypeValue(String skipUrlTypeValue) {
        this.skipUrlTypeValue = skipUrlTypeValue;
    }

    public String getWxValue() {
        return wxValue;
    }

    public void setWxValue(String wxValue) {
        this.wxValue = wxValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getPadValueByWxType(String wxType) {
        if (StringUtils.isEmpty(wxType)){
            return "";
        }
        if ("10".equals(wxType)){
            return "2";
        }

        if ("7".equals(wxType)){
            return "0";
        }
        return "1";
    }

}

