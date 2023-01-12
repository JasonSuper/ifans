package com.ifans.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.system.domain.SysUser;

import java.util.List;

/**
 * 用户 业务层
 */
public interface ISysUserService extends IService<SysUser> {

    SysUser selectUserByEmail(String email);

    SysUser selectUserById(String userId);

    String checkUserEmailUnique(SysUser sysUser);

    boolean registerUser(SysUser sysUser);

    int updateAvatar(String userId, String url);

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUnallocatedList(SysUser user);
}
