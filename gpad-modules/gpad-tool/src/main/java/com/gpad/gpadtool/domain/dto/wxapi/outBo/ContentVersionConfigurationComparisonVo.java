package com.gpad.gpadtool.domain.dto.wxapi.outBo;

public class ContentVersionConfigurationComparisonVo {
    /**
     * 版本管理表id
     */
    private Long contentVersionId;
    /**
     * 描述
     */
    private String describe;
    /**
     * id
     */
    private Long id;
    /**
     * 图标二
     */
    private String imageTwoUrl;
    /**
     * 图标
     */
    private String imageUrl;
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    /**
     * 是否高亮显示
     */
    private Long isHighlight;
    /**
     * 排序
     */
    private Long sort;

    public Long getContentVersionId() { return contentVersionId; }
    public void setContentVersionId(Long value) { this.contentVersionId = value; }

    public String getDescribe() { return describe; }
    public void setDescribe(String value) { this.describe = value; }

    public Long getid() { return id; }
    public void setid(Long value) { this.id = value; }

    public String getImageTwoUrl() { return imageTwoUrl; }
    public void setImageTwoUrl(String value) { this.imageTwoUrl = value; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String value) { this.imageUrl = value; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean value) { this.isDeleted = value; }

    public Long getIsHighlight() { return isHighlight; }
    public void setIsHighlight(Long value) { this.isHighlight = value; }

    public Long getSort() { return sort; }
    public void setSort(Long value) { this.sort = value; }
}