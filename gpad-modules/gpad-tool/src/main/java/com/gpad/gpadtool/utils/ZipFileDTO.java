package com.gpad.gpadtool.utils;

import lombok.Getter;
import lombok.Setter;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Setter
@Getter
public class ZipFileDTO {
    /**
     * 每个文件的文件名称
     */
    private List<String> fileNms;
    /**
     * 每个文件的流
     */
    private List<ByteArrayOutputStream> streams;
    /**
     * 定义的压缩文件的名称
     */
    private String zipFileNm;

    public ZipFileDTO() {
    }

    public ZipFileDTO(List<String> fileNms, List<ByteArrayOutputStream> streams, String zipFileNm) {
        this.fileNms = fileNms;
        this.streams = streams;
        this.zipFileNm = zipFileNm;
    }
}
