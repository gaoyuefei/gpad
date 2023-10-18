package com.gpad.gpadtool.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("scrm_role")
public class RoleInfo {
    @TableId
    private Long id;
    private Long parentId;
    private String roleName;
    private String roleAlias;
    private Integer isDeleted;

}
