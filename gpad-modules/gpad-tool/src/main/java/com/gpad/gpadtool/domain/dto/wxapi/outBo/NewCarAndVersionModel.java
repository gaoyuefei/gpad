package com.gpad.gpadtool.domain.dto.wxapi.outBo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCarAndVersionModel {
    /**
     * 预计提车结束时间
     */
    private Long appointCarEndTime;
    /**
     * 预计提车开始时间
     */
    private Long appointCarStartTime;
    /**
     * AR参数
     */
    private String ar;
    /**
     * 展厅大图-抖音小程序使用
     */
    private String bigCarPicture;
    /**
     * 车型简介
     */
    private String carIntroduction;
    /**
     * 车型列表图
     */
    private String carListPicture;
    /**
     * 商品名称
     */
    private String carName;
    /**
     * 商品图片
     */
    private String carPicture;
    /**
     * 车型类型：关联exhibition_car_type表 id
     */
    private Long carType;
    /**
     * 车型类型名称
     */
    private String carTypeName;
    /**
     * 版本数据集合
     */
    private ContentVersionVo[] contentVersionVoList;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品类型 1.o2o商品 2.c2m定制车商品 3.直订商品
     */
    private Long goodsType;
    /**
     * 主键
     */
    private Long id;
    /**
     * 删除标识 0未删除 1已删除
     */
    private Boolean isDeleted;
    /**
     * 横屏url
     */
    private String landscapeImgUrl;
    /**
     * 当前页码
     */
    private Long pageNo;
    /**
     * 每页个数
     */
    private Long pageSize;
    /**
     * 板块类型：0-新车商城
     */
    private Long plateType;
    /**
     * 是否显示卖点（true：显示，false：不显示）
     */
    private Boolean showSellingPoint;
    /**
     * 排序
     */
    private Long sort;
    /**
     * 全系标配
     */
    private String standardConfiguration;
    /**
     * 状态 ：1-已上架、2-已下架
     */
    private Long status;
    /**
     * 试驾链接
     */
    private String testDriveUrl;
    /**
     * 类型 1 线上展厅 2线下展厅
     */
    private Long type;
    /**
     * 修改人
     */
    private String updateBy;
    /**
     * 修改时间
     */
    private String updateTime;
    /**
     * 上架范围 0全部 1体验中心 2非体验中心
     */
    private Long upPort;
    /**
     * 车型code
     */
    private String vehicleCode;
    /**
     * 车型Id
     */
    private String vehicleId;
    /**
     * 车型名称
     */
    private String vehicleName;
    /**
     * VR展厅
     */
    private String vrUrl;
    /**
     * 心愿单图片
     */
    private String wishListPicture;




}