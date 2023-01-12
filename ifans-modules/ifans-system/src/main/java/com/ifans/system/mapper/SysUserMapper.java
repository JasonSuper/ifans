package com.ifans.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.system.domain.SysUser;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {
    
    List<SysUser> selectAllocatedList(SysUser user);

    List<SysUser> selectUnallocatedList(SysUser user);
}
