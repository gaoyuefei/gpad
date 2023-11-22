package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @description gpad_dlr_authentication_email_info
 * @author zhengkai.blog.csdn.net
 * @date 2023-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("gpad_dlr_authentication_email_info")
public class GpadDlrAuthenticationEmailInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    /**
    * id
    */
    private Long id;

    /**
    * 店代码
    */
    private String dealerCode;

    /**
    * 营业执照
    */
    private String uscc;

    /**
    * 企业邮箱
    */
    private String email;

    /**
    * 公司名称
    */
    private String companyName;

    /**
    * 申请信息人
    */
    private String createBy;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 最后修改人
    */
    private String updateBy;

    /**
    * 修改时间
    */
    private Date updateTime;

    /**
    * 删除标志
    */
    private String delFlag;

    /**
    * 版本号
    */
    private Integer version;

}