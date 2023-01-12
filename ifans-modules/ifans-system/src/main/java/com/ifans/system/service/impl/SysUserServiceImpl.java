package com.ifans.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.system.domain.SysUser;
import com.ifans.common.core.constant.UserConstants;
import com.ifans.datascope.annotation.DataScope;
import com.ifans.system.mapper.SysUserMapper;
import com.ifans.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 用户 业务层处理
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public SysUser selectUserByEmail(String email) {
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        query.eq(SysUser::getEmail, email);
        return userMapper.selectOne(query);
    }

    @Override
    public SysUser selectUserById(String userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public String checkUserEmailUnique(SysUser sysUser) {
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        query.eq(SysUser::getEmail, sysUser.getEmail());
        SysUser info = userMapper.selectOne(query);
        if (!Objects.isNull(info) && info.getEmail().equals(sysUser.getEmail())) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public boolean registerUser(SysUser sysUser) {
        return userMapper.insert(sysUser) > 0;
    }

    @Override
    public int updateAvatar(String userId, String url) {
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysUser::getAvatar, url).eq(SysUser::getId, userId);
        return userMapper.update(null, wrapper);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectAllocatedList(SysUser user) {
        return userMapper.selectAllocatedList(user);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUnallocatedList(SysUser user) {
        return userMapper.selectUnallocatedList(user);
    }
}
