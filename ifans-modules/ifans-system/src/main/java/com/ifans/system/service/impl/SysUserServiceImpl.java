package com.ifans.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.system.domain.SysRole;
import com.ifans.api.system.domain.SysUser;
import com.ifans.api.system.domain.SysUserRole;
import com.ifans.common.core.constant.UserConstants;
import com.ifans.common.core.exception.ServiceException;
import com.ifans.common.core.util.SpringUtils;
import com.ifans.common.core.util.StringUtils;
import com.ifans.common.security.util.SecurityUtils;
import com.ifans.datascope.annotation.DataScope;
import com.ifans.system.mapper.SysRoleMapper;
import com.ifans.system.mapper.SysUserMapper;
import com.ifans.system.mapper.SysUserRoleMapper;
import com.ifans.system.service.ISysPermissionService;
import com.ifans.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public SysUser selectUserByEmail(String email) {
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<>();
        query.eq(SysUser::getEmail, email);
        return userMapper.selectOne(query);
    }

    @Override
    public SysUser selectUserById(String userId) {
        SysUser sysUser = userMapper.selectById(userId);
        List<SysRole> roles = roleMapper.selectRolesByUserName(sysUser.getUserName());
        sysUser.setRoles(roles);
        return sysUser;
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


    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user) {
        return userMapper.selectUserList(user);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }


    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
//    @Override
//    public String selectUserPostGroup(String userName) {
//        List<SysPost> list = postMapper.selectPostsByUserName(userName);
//        if (CollectionUtils.isEmpty(list)) {
//            return StringUtils.EMPTY;
//        }
//        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
//    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(SysUser user) {
        String userId = StringUtils.isNull(user.getId()) ? "-1" : user.getId();
        SysUser info = userMapper.checkUserNameUnique(user.getUserName());
        if (StringUtils.isNotNull(info) && !info.getId().equals(userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        String userId = StringUtils.isNull(user.getId()) ? "-1" : user.getId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && !info.getId().equals(userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        String userId = StringUtils.isNull(user.getId()) ? "-1" : user.getId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && !info.getId().equals(userId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(String userId) {
        if (!SysUser.isAdmin(SecurityUtils.getUser().getId())) {
            SysUser user = new SysUser();
            user.setId(userId);
            List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user);
            if (StringUtils.isEmpty(users)) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser user) {
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户岗位关联
        //insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }


    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser user) {
        String userId = user.getId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        //userPostMapper.deleteUserPostByUserId(userId);
        // 新增用户与岗位管理
        //insertUserPost(user);
        return userMapper.updateUser(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserAuth(String userId, Long[] roleIds) {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        return userMapper.updateUserAvatar(userName, avatar) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password) {
        return userMapper.resetUserPwd(userName, password);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        this.insertUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
//    public void insertUserPost(SysUser user) {
//        Long[] posts = user.getPostIds();
//        if (StringUtils.isNotEmpty(posts)) {
//            // 新增用户与岗位管理
//            List<SysUserPost> list = new ArrayList<SysUserPost>();
//            for (Long postId : posts) {
//                SysUserPost up = new SysUserPost();
//                up.setUserId(user.getUserId());
//                up.setPostId(postId);
//                list.add(up);
//            }
//            userPostMapper.batchUserPost(list);
//        }
//    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(String userId, Long[] roleIds) {
        if (StringUtils.isNotEmpty(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.batchUserRole(list);
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserById(String userId) {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        //userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(String[] userIds) {
        for (String userId : userIds) {
            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        // 删除用户与岗位关联
        //userPostMapper.deleteUserPost(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

}
