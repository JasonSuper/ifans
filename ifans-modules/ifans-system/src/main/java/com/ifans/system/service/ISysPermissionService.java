package com.ifans.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.system.domain.SysRole;
import com.ifans.api.system.domain.SysUser;

import java.util.Set;

public interface ISysPermissionService extends IService<SysRole> {

    /**
     * 获取角色数据权限
     *
     * @param userId 用户Id
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user);

    /**
     * 获取菜单数据权限
     *
     * @param userId 用户Id
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user);
}
