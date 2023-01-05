package com.ifans.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.system.domain.SysRole;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRolePermissionByUserId(String userId);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    SysRole selectRoleById(Long roleId);
}
