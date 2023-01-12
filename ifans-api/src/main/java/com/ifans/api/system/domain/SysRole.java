package com.ifans.api.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ifans.common.core.util.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Set;

/**
 * 角色表 sys_role
 */
@Data
@NoArgsConstructor
@TableName("sys_role")
public class SysRole extends BaseEntity {
    private static final long serialVersionUID = 1L;

    public SysRole(Long roleId) {
        this.id = roleId;
    }

    /**
     * 角色ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 角色名称
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色权限
     */
    @TableField("role_key")
    private String roleKey;

    /**
     * 角色排序
     */
    @TableField("role_sort")
    private String roleSort;

    /**
     * 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）
     */
    @TableField("data_scope")
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    @TableField("menuCheckStrictly")
    private boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）
     */
    @TableField("deptCheckStrictly")
    private boolean deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    @TableField("status")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    @TableField("del_flag")
    private Integer delFlag;

    /**
     * 用户是否存在此角色标识 默认不存在
     */
    @TableField("flag")
    private boolean flag = false;

    /**
     * 菜单组
     */
    @TableField(exist = false)
    private Long[] menuIds;

    /**
     * 部门组（数据权限）
     */
    @TableField(exist = false)
    private Long[] deptIds;

    /**
     * 角色菜单权限
     */
    @TableField(exist = false)
    private Set<String> permissions;

    public boolean isAdmin() {
        return isAdmin(this.id);
    }

    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("roleId", getId())
                .append("roleName", getRoleName())
                .append("roleKey", getRoleKey())
                .append("roleSort", getRoleSort())
                .append("dataScope", getDataScope())
                .append("menuCheckStrictly", isMenuCheckStrictly())
                .append("deptCheckStrictly", isDeptCheckStrictly())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
