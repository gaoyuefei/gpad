package com.gpad.gpadtool.domain.dto.wxapi.outBo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentVersionVo {
    /**
     * 竞品对比品牌代码
     */
    private String cBrandCode;
    /**
     * 竞品对比品牌名称
     */
    private String cBrandName;
    /**
     * 竞品对比派生版本code
     */
    private String cDeriveCode;
    /**
     * 竞品对比车型版本
     */
    private String cDeriveName;
    /**
     * 竞品对比车系代码
     */
    private String cSerialCode;
    /**
     * 竞品对比车系名称
     */
    private String cSerialName;
    /**
     * 基础车型派生编号
     */
    private String deriveCode;
    /**
     * 基础车型派生id
     */
    private Long deriveId;
    /**
     * 派生名称
     */
    private String deriveName;
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * id
     */
    private Long id;
    /**
     * 图片url
     */
    private String imgUrl;
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    /**
     * 是否预售:false:不是,true:是
     */
    private Boolean isPreSale;
    /**
     * 版本简介
     */
    private String name;
    /**
     * 竞品对比开关0关闭 1开启
     */
    private Long otherFlag;
    /**
     * 当前页码
     */
    private Long pageNo;
    /**
     * 每页个数
     */
    private Long pageSize;
    /**
     * 动力类型
     */
    private String powerType;
    /**
     * 售价金额，单位分
     */
    private Long salePrice;
    /**
     * 排序
     */
    private Long sort;
    /**
     * 上下架状态  0下架 1上架
     */
    private Long status;
    /**
     * 车型id
     */
    private Long vehicleId;
    /**
     * 车型名称
     */
    private String vehicleName;
    /**
     * 版本
     */
    private String version;
    /**
     * 外观信息集合
     */
    private ContentVersionAppearanceVo[] versionAppearanceList;
    /**
     * 配置对比信息集合
     */
    private ContentVersionConfigurationComparisonVo[] versionConfigurationComparisonList;

    /**
     * 内饰信息集合
     */
    private ContentVersionInteriorVo[] versionInteriorList;

    /**
     * 车型版本名称
     */
    private String versionName;


}