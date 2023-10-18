package com.gpad.gpadtool.constant;


import lombok.Getter;


@Getter
public enum FileType {

    //文件类型：1=图片；2=视频；3=PDF;4=发票

    PICTURE(1, "图片"),
    VIDEO(2, "视频"),
    PDF(3, "PDF"),
    INVOICE(4, "发票");

    private Integer code;

    private String info;

    FileType(Integer code, String info) {
        this.code = code;
        this.info = info;
    }
}
