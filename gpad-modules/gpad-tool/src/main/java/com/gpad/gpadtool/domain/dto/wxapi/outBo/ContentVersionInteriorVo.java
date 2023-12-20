package com.gpad.gpadtool.domain.dto.wxapi.outBo;

public class ContentVersionInteriorVo {
    /**
     * 颜色编码
     */
    private String colorCode;
    /**
     * 颜色id
     */
    private Long colorId;
    /**
     * 颜色名称
     */
    private String colorName;
    /**
     * 颜色图片
     */
    private String colorUrl;
    /**
     * 版本管理表id
     */
    private Long contentVersionId;
    /**
     * id
     */
    private Long id;
    /**
     * 车颜色图片(竖屏)
     */
    private String imageUrl;
    /**
     * 车颜色图片(横屏)
     */
    private String imageUrlTwo;
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    /**
     * 分类 ：1横屏 2竖屏
     */
    private Long type;

    public String getColorCode() { return colorCode; }
    public void setColorCode(String value) { this.colorCode = value; }

    public Long getColorId() { return colorId; }
    public void setColorId(Long value) { this.colorId = value; }

    public String getColorName() { return colorName; }
    public void setColorName(String value) { this.colorName = value; }

    public String getColorUrl() { return colorUrl; }
    public void setColorUrl(String value) { this.colorUrl = value; }

    public Long getContentVersionId() { return contentVersionId; }
    public void setContentVersionId(Long value) { this.contentVersionId = value; }

    public Long getid() { return id; }
    public void setid(Long value) { this.id = value; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String value) { this.imageUrl = value; }

    public String getImageUrlTwo() { return imageUrlTwo; }
    public void setImageUrlTwo(String value) { this.imageUrlTwo = value; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean value) { this.isDeleted = value; }

    public Long getType() { return type; }
    public void setType(Long value) { this.type = value; }
}