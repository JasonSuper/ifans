package com.ifans.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.system.domain.SysMenu;

import java.util.Collection;
import java.util.Set;

/**
 * 菜单 业务层
 */
public interface ISysMenuService extends IService<SysMenu> {


    Set<String> selectMenuPermsByRoleId(String id);

    Collection<String> selectMenuPermsByUserId(String id);
}
