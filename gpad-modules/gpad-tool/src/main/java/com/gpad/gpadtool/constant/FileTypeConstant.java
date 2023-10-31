package com.gpad.gpadtool.constant;

import lombok.Getter;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName FileTypeConstant.java
 * @Description FileTypeConstant
 * @createTime 2021年10月02日 16:18:00
 */
@Getter
public enum FileTypeConstant {

    VIDEO_TYPE_MP4(FileType.VIDEO,"mp4"),
    VIDEO_TYPE_AVI(FileType.VIDEO,"avi"),
    VIDEO_TYPE_WMV(FileType.VIDEO,"wmv"),
    VIDEO_TYPE_MPG(FileType.VIDEO,"mpg"),
    VIDEO_TYPE_MPEG(FileType.VIDEO,"mpeg"),
    VIDEO_TYPE_MOV(FileType.VIDEO,"mov"),
    IMG_TYPE_JPG(FileType.PICTURE,"jpg"),
    IMG_TYPE_JPEG(FileType.PICTURE,"jpeg"),
    IMG_TYPE_PNG(FileType.PICTURE,"png"),
    IMAGE_PNG_TYPE_PNG(FileType.PICTURE,"image/png"),
    IMG_TYPE_GIF(FileType.PICTURE,"gif"),
    FILE_TYPE_PDF(FileType.PDF,"pdf"),
    XLSX(FileType.INVOICE,"xlsx"),
    FILE_TYPE_CSV(FileType.INVOICE,"csv");

    private final FileType code;
    private final String value;


    FileTypeConstant(FileType code, String value) {
        this.code = code;
        this.value = value;
    }

    public static FileType getFileTypeByValue(String value){
        for (FileTypeConstant constant :
                FileTypeConstant.values()) {
            if (value.equalsIgnoreCase(constant.value)) {
                return constant.code;
            }
        }
        return null;
    }

    public static boolean checkFileType(String type){
        for (FileTypeConstant constant :
                FileTypeConstant.values()) {
            if (constant.value.contains(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "FileTypeConstant{}";
    }
}
