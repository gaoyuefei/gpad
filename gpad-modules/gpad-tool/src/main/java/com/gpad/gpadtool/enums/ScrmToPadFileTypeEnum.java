package com.gpad.gpadtool.enums;

/**
 * SCRM  1：PDF  2：图片  3：视频
 * @ClassName GrtToPadEnums
 * @Date: 2022/9/21 9:53
 * @Version: 1.0
 * @Author: LF
 */
public enum  ScrmToPadFileTypeEnum {

    /**
     * 未配车
     */
    DEFAULT_PDF_TYPE("3", "PDF","1"),

    /**
     * 未提交
     */
    DEFAULT_IMAGE_TYPE("1", "图片","2"),
    /**
     * 经理审核中
     */
    DEFAULT_IMAGE_VIDEO("2", "视频","3"),
    ;
    private String padValue;

    private String scrmValue;

    private String desc;

    ScrmToPadFileTypeEnum(String padValue,  String desc ,String scrmValue) {
        this.padValue = padValue;
        this.scrmValue = scrmValue;
        this.desc = desc;
    }

    public String getPadValue() {
        return padValue;
    }

    public void setPadValue(String padValue) {
        this.padValue = padValue;
    }

    public String getScrmValue() {
        return scrmValue;
    }

    public void setScrmValue(String scrmValue) {
        this.scrmValue = scrmValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getPadValueByScrmType(String scrmType) {
        ScrmToPadFileTypeEnum[] values = ScrmToPadFileTypeEnum.values();
        for (ScrmToPadFileTypeEnum scrmToPadFileTypeEnums : values) {
            if(scrmType != null && scrmType.equals(scrmToPadFileTypeEnums.getScrmValue())){
                return scrmToPadFileTypeEnums.getPadValue();
            }
        }
        return "";
    }

}

