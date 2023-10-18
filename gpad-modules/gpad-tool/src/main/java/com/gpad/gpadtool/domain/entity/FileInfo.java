package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("file_info")
public class FileInfo {

    @TableId
    private String id;
    private String businessNo;
    private Integer linkType;
    private String coverImgId;
    private String title;
    private String filePath;
    private String fileName;
    private Integer fileType;
    private String suitCarType;
    private Integer status;
    private Integer orderNum;
    private Date createTime;
    private Date updateTime;
}
