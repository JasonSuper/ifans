package com.ifans.common.security.service;

import com.ifans.api.system.FeignUserService;
import com.ifans.api.system.model.LoginUser;
import com.ifans.common.core.constant.CacheConstants;
import com.ifans.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * 用户详细信息
 */
@Slf4j
@Primary
@RequiredArgsConstructor
public class IfansUserDetailsServiceImpl implements IfansUserDetailsService {

    private final FeignUserService feignUserService;
    private final CacheManager cacheManager;

    /**
     * 用户名密码登录
     *
     * @param username 用户名
     * @return
     */
    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(String username) {
        Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
        if (cache != null && cache.get(username) != null) {
            return (IfansUser) cache.get(username).get();
        }

        R<LoginUser> result = feignUserService.getUserInfo(username);
        UserDetails userDetails = getUserDetails(result);
        if (cache != null) {
            cache.put(username, userDetails);
        }
        return userDetails;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
