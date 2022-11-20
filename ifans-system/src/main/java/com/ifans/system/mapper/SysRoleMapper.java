package com.ifans.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.system.domain.SysRole;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRolePermissionByUserId(String userId);
}
