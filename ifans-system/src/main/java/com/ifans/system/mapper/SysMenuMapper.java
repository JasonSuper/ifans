package com.ifans.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.system.domain.SysMenu;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<String> selectMenuPermsByRoleId(String id);

    List<String> selectMenuPermsByUserId(String id);
}
