package com.ifans.system.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ifans.api.system.domain.SysUser;
import com.ifans.api.system.model.LoginUser;
import com.ifans.common.core.constant.UserConstants;
import com.ifans.common.core.util.R;
import com.ifans.common.oss.OssService;
import com.ifans.common.security.annotation.Inner;
import com.ifans.common.security.util.SecurityUtils;
import com.ifans.system.service.ISysPermissionService;
import com.ifans.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysPermissionService permissionService;

    @Autowired
    private OssService ossService;

    /**
     * 获取当前用户信息
     */
    @Inner
    @GetMapping("/info/{username}")
    public R<LoginUser> info(@PathVariable("username") String email) {
        SysUser sysUser = sysUserService.selectUserByEmail(email);
        if (Objects.isNull(sysUser)) {
            return R.failed("用户名或密码错误");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser);
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public R<Map<String, Object>> getInfo() {
        SysUser user = sysUserService.selectUserById(SecurityUtils.getUser().getId());
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", user);
        resultMap.put("roles", roles);
        resultMap.put("permissions", permissions);
        return R.ok(resultMap);
    }

    /**
     * 注册账号
     */
    @Inner
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody SysUser sysUser) {
        String username = sysUser.getUserName();
        if (UserConstants.NOT_UNIQUE.equals(sysUserService.checkUserEmailUnique(sysUser))) {
            return R.failed("保存用户'" + username + "'失败，注册账号已存在");
        }
        return R.ok(sysUserService.registerUser(sysUser));
    }

    /**
     * 上传头像到阿里云OSS
     *
     * @param file 头像文件
     */
    @PostMapping("/updateAvatar")
    public R updateAvatar(MultipartFile file) {
        try {
            //获取上传文件输入流
            InputStream inputStream = file.getInputStream();
            //获取文件名称
            String fileName = file.getOriginalFilename();
            //获取上传的文件  通过 MultipartFile
            String url = ossService.simpleUpload(fileName, "avatar", inputStream);//返回上传图片的路径

            if (StringUtils.isNotEmpty(url)) {
                // 入库
                sysUserService.updateAvatar(SecurityUtils.getUser().getId(), url);
                return R.ok(url, "上传头像成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.failed("修改头像失败");
    }

    @PostMapping("/editInfo")
    public R editInfo(@RequestBody SysUser sysUser) {
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysUser::getUserName, sysUser.getUserName()).set(SysUser::getSex, sysUser.getSex()).eq(SysUser::getId, SecurityUtils.getUser().getId());
        boolean isOK = sysUserService.update(wrapper);
        if (isOK) {
            return R.ok();
        }
        return R.failed();
    }
}
