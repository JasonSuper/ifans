package com.ifans.system.controller;

import com.ifans.api.system.domain.SysRole;
import com.ifans.api.system.domain.SysUser;
import com.ifans.api.system.domain.SysUserRole;
import com.ifans.common.core.constant.UserConstants;
import com.ifans.common.core.util.poi.ExcelUtil;
import com.ifans.common.core.web.controller.BaseController;
import com.ifans.common.core.web.domain.AjaxResult;
import com.ifans.common.core.web.page.TableDataInfo;
import com.ifans.common.security.util.SecurityUtils;
import com.ifans.system.service.ISysRoleService;
import com.ifans.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 角色信息
 */
@RestController
@RequestMapping("/role")
public class SysRoleController extends BaseController {

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysUserService userService;

//    @Autowired
//    private ISysDeptService deptService;

    @PreAuthorize("@pms.hasPermission('system:role:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRole role) {
        startPage();
        List<SysRole> list = roleService.selectRoleList(role);
        return getDataTable(list);
    }

    /**
     * 角色管理
     */
    @PreAuthorize("@pms.hasPermission('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRole role) {
        List<SysRole> list = roleService.selectRoleList(role);
        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@pms.hasPermission('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public AjaxResult getInfo(@PathVariable Long roleId) {
        roleService.checkRoleDataScope(roleId);
        return AjaxResult.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@pms.hasPermission('system:role:add')")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysRole role) {
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return AjaxResult.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(SecurityUtils.getUser().getUsername());
        return toAjax(roleService.insertRole(role));

    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getId());
        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
            return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
            return AjaxResult.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(SecurityUtils.getUser().getUsername());
        return toAjax(roleService.updateRole(role));
    }

    /**
     * 修改保存数据权限
     */
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    @PutMapping("/dataScope")
    public AjaxResult dataScope(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getId());
        return toAjax(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getId());
        role.setUpdateBy(SecurityUtils.getUser().getUsername());
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
    @PreAuthorize("@pms.hasPermission('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds) {
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    @PreAuthorize("@pms.hasPermission('system:role:query')")
    @GetMapping("/optionselect")
    public AjaxResult optionselect() {
        return AjaxResult.success(roleService.selectRoleAll());
    }

    /**
     * 查询已分配用户角色列表
     */
    @PreAuthorize("@pms.hasPermission('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo allocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectAllocatedList(user);
        return getDataTable(list);
    }

    /**
     * 查询未分配用户角色列表
     */
    @PreAuthorize("@pms.hasPermission('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo unallocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUnallocatedList(user);
        return getDataTable(list);
    }

    /**
     * 取消授权用户
     */
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    @PutMapping("/authUser/cancel")
    public AjaxResult cancelAuthUser(@RequestBody SysUserRole userRole) {
        return toAjax(roleService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     */
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    @PutMapping("/authUser/cancelAll")
    public AjaxResult cancelAuthUserAll(Long roleId, String[] userIds) {
        return toAjax(roleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     */
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    @PutMapping("/authUser/selectAll")
    public AjaxResult selectAuthUserAll(Long roleId, String[] userIds) {
        roleService.checkRoleDataScope(roleId);
        return toAjax(roleService.insertAuthUsers(roleId, userIds));
    }

//    /**
//     * 获取对应角色部门树列表
//     */
//    @PreAuthorize("@pms.hasPermission('system:role:query')")
//    @GetMapping(value = "/deptTree/{roleId}")
//    public AjaxResult deptTree(@PathVariable("roleId") Long roleId) {
//        AjaxResult ajax = AjaxResult.success();
//        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
//        ajax.put("depts", deptService.selectDeptTreeList(new SysDept()));
//        return ajax;
//    }
}
