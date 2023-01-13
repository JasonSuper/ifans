package com.ifans.system.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ifans.api.system.domain.SysDept;
import com.ifans.api.system.domain.SysRole;
import com.ifans.api.system.domain.SysUser;
import com.ifans.api.system.model.LoginUser;
import com.ifans.common.core.constant.UserConstants;
import com.ifans.common.core.util.R;
import com.ifans.common.core.util.StringUtils;
import com.ifans.common.core.web.controller.BaseController;
import com.ifans.common.core.web.domain.AjaxResult;
import com.ifans.common.core.web.page.TableDataInfo;
import com.ifans.common.security.annotation.Inner;
import com.ifans.common.security.util.SecurityUtils;
import com.ifans.system.service.ISysDeptService;
import com.ifans.system.service.ISysPermissionService;
import com.ifans.system.service.ISysRoleService;
import com.ifans.system.service.ISysUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.ifans.common.core.util.PageUtils.startPage;
import static com.ifans.common.core.web.domain.AjaxResult.success;

@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysPermissionService permissionService;
    @Autowired
    private ISysRoleService roleService;
    @Autowired
    private ISysDeptService deptService;

//    @Autowired
//    private OssService ossService;

    /**
     * 获取当前用户信息
     */
    @Inner
    @GetMapping("/info/{username}")
    public R<LoginUser> info(@PathVariable("username") String email) {
        SysUser sysUser = userService.selectUserByEmail(email);
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
        SysUser user = userService.selectUserById(SecurityUtils.getUser().getId());
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

//    /**
//     * 上传头像到阿里云OSS
//     *
//     * @param file 头像文件
//     */
//    @PostMapping("/updateAvatar")
//    public R updateAvatar(MultipartFile file) {
//        try {
//            //获取上传文件输入流
//            InputStream inputStream = file.getInputStream();
//            //获取文件名称
//            String fileName = file.getOriginalFilename();
//            //获取上传的文件  通过 MultipartFile
//            String url = ossService.simpleUpload(fileName, "avatar", inputStream);//返回上传图片的路径
//
//            if (StringUtils.isNotEmpty(url)) {
//                // 入库
//                sysUserService.updateAvatar(SecurityUtils.getUser().getId(), url);
//                return R.ok(url, "上传头像成功");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return R.failed("修改头像失败");
//    }

    @PostMapping("/editInfo")
    public R editInfo(@RequestBody SysUser sysUser) {
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysUser::getUserName, sysUser.getUserName())
                .set(SysUser::getSex, sysUser.getSex())
                .set(SysUser::getAvatar, sysUser.getAvatar())
                .eq(SysUser::getId, SecurityUtils.getUser().getId());
        boolean isOK = userService.update(wrapper);
        if (isOK) {
            return R.ok();
        }
        return R.failed();
    }


    /**
     * 获取用户列表
     */
    @PreAuthorize("@pms.hasPermission('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

//    @PreAuthorize("@pms.hasPermission('system:user:export')")
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, SysUser user) {
//        List<SysUser> list = userService.selectUserList(user);
//        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
//        util.exportExcel(response, list, "用户数据");
//    }

//    @PreAuthorize("@pms.hasPermission('system:user:import')")
//    @PostMapping("/importData")
//    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
//        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
//        List<SysUser> userList = util.importExcel(file.getInputStream());
//        String operName = SecurityUtils.getUsername();
//        String message = userService.importUser(userList, updateSupport, operName);
//        return success(message);
//    }

//    @PostMapping("/importTemplate")
//    public void importTemplate(HttpServletResponse response) throws IOException {
//        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
//        util.importTemplateExcel(response, "用户数据");
//    }

    /**
     * 注册用户信息
     */
    @Inner
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody SysUser sysUser) {
        String username = sysUser.getUserName();
//        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
//            return R.failed("当前系统没有开启注册功能！");
//        }
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(sysUser))) {
            return R.failed("保存用户'" + username + "'失败，注册账号已存在");
        }
        return R.ok(userService.registerUser(sysUser));
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@pms.hasPermission('system:user:query')")
    @GetMapping(value = {"/", "/{id}"})
    public AjaxResult getInfo(@PathVariable(value = "id", required = false) String id) {
        userService.checkUserDataScope(id);
        AjaxResult ajax = success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(id) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        //ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(id) && !id.equals("-9999")) {
            SysUser sysUser = userService.selectUserById(id);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            //ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", sysUser.getRoles().stream().map(sysRole -> sysRole.getId() + "").collect(Collectors.toList()));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@pms.hasPermission('system:user:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(SecurityUtils.getUser().getUsername());
        user.setPassword(ENCODER.encode(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@pms.hasPermission('system:user:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUser().getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@pms.hasPermission('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable String[] userIds) {
        if (ArrayUtils.contains(userIds, SecurityUtils.getUser().getId())) {
            return AjaxResult.error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@pms.hasPermission('system:user:edit')")
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        user.setPassword(ENCODER.encode(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUser().getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@pms.hasPermission('system:user:edit')")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        user.setUpdateBy(SecurityUtils.getUser().getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@pms.hasPermission('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") String userId) {
        AjaxResult ajax = success();
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@pms.hasPermission('system:user:edit')")
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(String userId, Long[] roleIds) {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@pms.hasPermission('system:user:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept) {
        return AjaxResult.success(deptService.selectDeptTreeList(dept));
    }
}
