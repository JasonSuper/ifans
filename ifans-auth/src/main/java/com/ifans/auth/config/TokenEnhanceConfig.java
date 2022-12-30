package com.ifans.auth.config;

import cn.hutool.core.map.MapUtil;
import com.ifans.auth.domain.LoginUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;

/**
 * JWT Claim 内容增强
 */
@Configuration
@RequiredArgsConstructor
public class TokenEnhanceConfig {

    private final RedisTemplate redisTemplate;

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Object principal = authentication.getUserAuthentication().getPrincipal();
            Map<String, Object> additionalInfo = MapUtil.newHashMap();
            if (principal instanceof LoginUserDetails) {
                LoginUserDetails loginUserDetails = (LoginUserDetails) principal;
                additionalInfo.put("userId", loginUserDetails.getLoginUser().getSysUser().getId());
                additionalInfo.put("username", loginUserDetails.getUsername());
                //additionalInfo.put("deptId", "123");
                //additionalInfo.put("dataScope", "0");

                /**
                 * 系统用户按钮权限标识数据量多存放至redis
                 *
                 * key:AUTH:USER_PERMS:2
                 * value:['sys:user:add',...]
                 */
                redisTemplate.opsForValue().set("AUTH:USER_PERMS:" + loginUserDetails.getLoginUser().getSysUser().getId(), loginUserDetails.getLoginUser().getPermissions());
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        };
    }
}
