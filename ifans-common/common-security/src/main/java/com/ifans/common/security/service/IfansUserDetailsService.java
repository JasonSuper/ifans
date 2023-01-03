package com.ifans.common.security.service;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.ifans.api.system.domain.SysUser;
import com.ifans.api.system.model.LoginUser;
import com.ifans.common.core.constant.CommonConstants;
import com.ifans.common.core.constant.SecurityConstants;
import com.ifans.common.core.util.R;
import com.ifans.common.core.util.RetOps;
import org.springframework.core.Ordered;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface IfansUserDetailsService extends UserDetailsService, Ordered {

    /**
     * 是否支持此客户端校验
     *
     * @param clientId 目标客户端
     * @return true/false
     */
    default boolean support(String clientId, String grantType) {
        return true;
    }

    /**
     * 排序值 默认取最大的
     *
     * @return 排序值
     */
    default int getOrder() {
        return 0;
    }

    /**
     * 构建userdetails
     *
     * @param result 用户信息
     * @return UserDetails
     */
    default UserDetails getUserDetails(R<LoginUser> result) {
        LoginUser info = RetOps.of(result).getData().orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        Set<String> dbAuthsSet = new HashSet<>();

        if (ArrayUtil.isNotEmpty(info.getRoles())) {
            // 获取角色
            info.getRoles().forEach(role -> dbAuthsSet.add(SecurityConstants.ROLE + role));
            // 获取资源
            dbAuthsSet.addAll(info.getPermissions());
        }

        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(dbAuthsSet.toArray(new String[0]));
        SysUser user = info.getSysUser();

        // 构造security用户
        return new IfansUser(user.getId(), user.getDeptId(), user.getEmail(),
                SecurityConstants.BCRYPT + user.getPassword(), user.getPhonenumber(), true, true, true,
                StrUtil.equals(user.getStatus(), CommonConstants.STATUS_NORMAL), authorities);
    }

    /**
     * 通过用户实体查询
     *
     * @param ifansUser user
     * @return
     */
    default UserDetails loadUserByUser(IfansUser ifansUser) {
        return this.loadUserByUsername(ifansUser.getUsername());
    }

}
