package com.gpad.gpadtool.constant;


import lombok.Getter;


@Getter
public enum FileLinkType {

    //关联类型：1=新车PDI；21=到店检查-外观检查；22=到店检查-其他检查项；31=交车确认-外观检查；32=交车确认-其他检查项；4=签名PDF；5=用车指南；6=标准检查流程演示；7=封面图片；8=发票文件

    PDI(1, "新车PDI"),
    CHECK_OUT(21, "到店检查-外观检查"),
    CHECK_OTHER(22, "到店检查-其他检查项"),
    CONFIRM_OUT(31, "交车确认-外观检查"),
    CONFIRM_OTHER(32, "交车确认-其他检查项"),
    CONFIRM_SIGN(33, "交车确认-君子签签名"),
    SIGN(4, "签名PDF"),
    GUIDE(5, "用车指南"),
    CHECK_FLOW(6, "标准检查流程演示"),
    COVER(7, "封面图片"),
    INVOICE(8, "发票文件");

    private Integer code;

    private String info;

    FileLinkType(Integer code, String info) {
        this.code = code;
        this.info = info;
    }
}
