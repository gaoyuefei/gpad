package com.gpad.gpadtool.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Donald.Lee
 * @version 1.0.0
 * @ClassName FileInfoDto.java
 * @Description 文件Dto
 * @createTime 2023年09月21日 16:00:00
 */
@Data
@ApiModel(value = "FileInfoDto",description = "文件Dto")
public class FileInfoDto {
    @ApiModelProperty(value =  "id")
    private String id;
    @ApiModelProperty(value =  "订单号")
    private String businessNo;
    @ApiModelProperty(value =  "关联类型：1=新车PDI；2=到店检查；3=交车确认；4=签名PDF；5=用车指南；6=标准检查流程演示；7=封面图片")
    private Integer linkType;
    @ApiModelProperty(value =  "封面图片ID")
    private String coverImgId;
    @ApiModelProperty(value =  "标题")
    private String title;
    @ApiModelProperty(value =  "图片在文件服务器的存储位置或路径")
    private String filePath;
    @ApiModelProperty(value =  "文件名")
    private String fileName;
    @ApiModelProperty(value =  "文件类型：1=图片；2=视频；3=PDF；4=发票")
    private Integer fileType;
    @ApiModelProperty(value =  "适用车型")
    private String suitCarType;
    @ApiModelProperty(value =  "状态=1：可用；0：不可用")
    private Integer status;
    @ApiModelProperty(value =  "排序值")
    private Integer orderNum;
    private Date createTime;
    private Date updateTime;

    public FileInfoDto() {
    }

    public FileInfoDto(String id, String businessNo, Integer linkType, String coverImgId, String title, String filePath, String fileName, Integer fileType, String suitCarType, Integer status, Integer orderNum, Date createTime, Date updateTime) {
        this.id = id;
        this.businessNo = businessNo;
        this.linkType = linkType;
        this.coverImgId = coverImgId;
        this.title = title;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileType = fileType;
        this.suitCarType = suitCarType;
        this.status = status;
        this.orderNum = orderNum;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public FileInfoDto(String businessNo, Integer linkType, String filePath, String fileName, Integer fileType) {
        this.businessNo = businessNo;
        this.linkType = linkType;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileType = fileType;
    }
}
