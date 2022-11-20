package com.ifans.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.system.domain.SysRole;
import com.ifans.api.system.domain.SysUser;

import java.util.Set;

/**
 * 用户 业务层
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectRolePermissionByUserId(String userId);
}
