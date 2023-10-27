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
    private String bussinessNo;
    @ApiModelProperty(value =  "关联类型：1=新车PDI；21=交车准备-车况检查；22=交车准备-交车仪式；31=产品签名图片")
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

    public FileInfoDto(String id, String bussinessNo, Integer linkType, String coverImgId, String title, String filePath, String fileName, Integer fileType, String suitCarType, Integer status, Integer orderNum, Date createTime, Date updateTime) {
        this.id = id;
        this.bussinessNo = bussinessNo;
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

    public FileInfoDto(String bussinessNo, Integer linkType, String filePath, String fileName, Integer fileType) {
        this.bussinessNo = bussinessNo;
        this.linkType = linkType;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileType = fileType;
    }
}
