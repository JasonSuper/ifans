package com.ifans.common.security.service;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ifans.api.system.domain.SysRole;
import com.ifans.api.system.domain.SysUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 扩展用户信息
 */
public class IfansUser extends User implements OAuth2AuthenticatedPrincipal {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	/**
	 * 用户ID
	 */
	@Getter
	@JsonSerialize(using = ToStringSerializer.class)
	private final String id;

	/**
	 * 部门ID
	 */
	@Getter
	@JsonSerialize(using = ToStringSerializer.class)
	private final String deptId;

	/**
	 * 手机号
	 */
	@Getter
	private final String phone;

	/**
	 * 用户信息
	 */
	@Getter
	private SysUser sysUser;

	public IfansUser(String id, String deptId, String username, String password, String phone, boolean enabled,
                     boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
                     Collection<? extends GrantedAuthority> authorities, SysUser sysUser) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.id = id;
		this.deptId = deptId;
		this.phone = phone;
		this.sysUser = sysUser;
	}

	/**
	 * Get the OAuth 2.0 token attributes
	 * @return the OAuth 2.0 token attributes
	 */
	@Override
	public Map<String, Object> getAttributes() {
		return new HashMap<>();
	}

	@Override
	public String getName() {
		return this.getUsername();
	}

}
