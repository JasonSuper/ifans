package com.ifans.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.system.domain.SysMenu;
import com.ifans.system.mapper.SysMenuMapper;
import com.ifans.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public Set<String> selectMenuPermsByRoleId(String id) {
        List<String> perms = sysMenuMapper.selectMenuPermsByRoleId(id);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StrUtil.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public Collection<String> selectMenuPermsByUserId(String id) {
        List<String> perms = sysMenuMapper.selectMenuPermsByUserId(id);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StrUtil.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }
}
