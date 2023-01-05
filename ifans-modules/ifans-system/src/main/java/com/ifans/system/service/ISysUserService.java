package com.ifans.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.system.domain.SysUser;

/**
 * 用户 业务层
 */
public interface ISysUserService extends IService<SysUser> {

    SysUser selectUserByEmail(String email);

    SysUser selectUserById(String userId);

    String checkUserEmailUnique(SysUser sysUser);

    boolean registerUser(SysUser sysUser);

    int updateAvatar(String userId, String url);
}
