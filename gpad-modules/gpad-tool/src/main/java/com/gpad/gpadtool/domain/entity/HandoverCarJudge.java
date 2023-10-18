package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("handover_car_judge")
public class HandoverCarJudge implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * 订单号
     */
    private String businessNo;

    /**
     * 满意度：1--5
     */
    private Integer satisfaction;

    /**
     * app售后权益是否激活：1是0否
     */
    private Integer hasAfterSalesBenefits;

    /**
     * 讲解是否清楚：1是0否
     */
    private Integer isClear;

    /**
     * 是否愿意推荐：1是0否
     */
    private Integer isRecommend;

    /**
     * 客户id
     */
    private Long userId;

    /**
     * 客户编码
     */
    private String userCode;

    /**
     * 客户名称
     */
    private String userName;

    /**
     * 评价时间
     */
    private Date createTime;
}
