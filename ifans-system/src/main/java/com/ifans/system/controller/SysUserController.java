package com.ifans.system.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ifans.api.system.domain.SysUser;
import com.ifans.api.system.model.LoginUser;
import com.ifans.common.core.annotation.InnerAuth;
import com.ifans.common.core.constant.UserConstants;
import com.ifans.common.core.domain.R;
import com.ifans.common.core.oss.OssService;
import com.ifans.common.core.utils.SecurityUtils;
import com.ifans.common.core.utils.StringUtils;
import com.ifans.common.core.web.domain.AjaxResult;
import com.ifans.system.service.ISysPermissionService;
import com.ifans.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
    @InnerAuth
    @GetMapping("/info/{username}")
    public R<LoginUser> info(@PathVariable("username") String email) {
        SysUser sysUser = sysUserService.selectUserByEmail(email);
        if (StringUtils.isNull(sysUser)) {
            return R.fail("用户名或密码错误");
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
    public AjaxResult getInfo() {
        SysUser user = sysUserService.selectUserById(SecurityUtils.getUserId());
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 注册账号
     */
    @InnerAuth
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody SysUser sysUser) {
        String username = sysUser.getUserName();
        if (UserConstants.NOT_UNIQUE.equals(sysUserService.checkUserEmailUnique(sysUser))) {
            return R.fail("保存用户'" + username + "'失败，注册账号已存在");
        }
        return R.ok(sysUserService.registerUser(sysUser));
    }

    /**
     * 上传头像到阿里云OSS
     *
     * @param file   头像文件
     */
    @PostMapping("/updateAvatar")
    public R updateAvatar( MultipartFile file) {
        try {
            //获取上传文件输入流
            InputStream inputStream = file.getInputStream();
            //获取文件名称
            String fileName = file.getOriginalFilename();
            //获取上传的文件  通过 MultipartFile
            String url = ossService.simpleUpload(fileName, "avatar", inputStream);//返回上传图片的路径

            if (StringUtils.isNotEmpty(url)) {
                // 入库
                sysUserService.updateAvatar(SecurityUtils.getUserId(), url);
                return R.ok(url, "上传头像成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.fail("修改头像失败");
    }

    @PostMapping("/editInfo")
    public R editInfo(@RequestBody SysUser sysUser) {
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysUser::getUserName, sysUser.getUserName()).set(SysUser::getSex, sysUser.getSex()).eq(SysUser::getId, SecurityUtils.getUserId());
        boolean isOK = sysUserService.update(wrapper);
        if (isOK) {
            return R.ok();
        }
        return R.fail();
    }
}
