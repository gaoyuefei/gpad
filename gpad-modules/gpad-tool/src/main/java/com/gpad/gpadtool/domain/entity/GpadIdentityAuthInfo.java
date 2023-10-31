package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@TableName("gpad_identity_auth_info")
public class GpadIdentityAuthInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    /**
    * 主键
    */
    private Long id;

    /**
    * 订单号
    */
    private String bussinessNo;

    /**
    * 实名账号
    */
    private String account;

    /**
    * 身份证号
    */
    private String identityCard;

    /**
    * 身份证类型
    */
    private String identityType;

    /**
    * 产品专家名字
    */
    private String productName;

    /**
    * 产品专家代码
    */
    private String productCode;

    /**
    * 产品专家电话号码
    */
    private String productMobile;

    /**
    * 签名路径
    */
    private String filePath;

    /**
    * version
    */
    private int version;

    /**
    * 备注
    */
    private String remark;

    /**
    * 签名路径
    */
    private int delFlag;

    /**
    * create_time
    */
    private Date createTime;

    /**
    * update_time
    */
    private Date updateTime;

}