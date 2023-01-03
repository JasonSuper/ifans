package com.ifans.common.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户详细信息
 *
 * @author lengleng hccake
 */
@Slf4j
@RequiredArgsConstructor
public class IfansAppUserDetailsServiceImpl {//implements IfansUserDetailsService {

//	private final RemoteUserService remoteUserService;
//
//	private final CacheManager cacheManager;
//
//	/**
//	 * 手机号登录
//	 * @param phone 手机号
//	 * @return
//	 */
//	@Override
//	@SneakyThrows
//	public UserDetails loadUserByUsername(String phone) {
//		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
//		if (cache != null && cache.get(phone) != null) {
//			return (PigUser) cache.get(phone).get();
//		}
//
//		R<UserInfo> result = remoteUserService.infoByMobile(phone);
//
//		UserDetails userDetails = getUserDetails(result);
//		if (cache != null) {
//			cache.put(phone, userDetails);
//		}
//		return userDetails;
//	}
//
//	/**
//	 * check-token 使用
//	 * @param pigUser user
//	 * @return
//	 */
//	@Override
//	public UserDetails loadUserByUser(PigUser pigUser) {
//		return this.loadUserByUsername(pigUser.getPhone());
//	}
//
//	/**
//	 * 是否支持此客户端校验
//	 * @param clientId 目标客户端
//	 * @return true/false
//	 */
//	@Override
//	public boolean support(String clientId, String grantType) {
//		return SecurityConstants.APP.equals(grantType);
//	}

}
