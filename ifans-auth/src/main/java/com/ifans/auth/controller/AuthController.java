package com.ifans.auth.controller;

import cn.hutool.json.JSONUtil;
import com.ifans.api.system.fallback.FeignUserFallbackFactory;
import com.ifans.auth.utils.RequestUtils;
import com.ifans.common.core.domain.R;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
//@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final TokenEndpoint tokenEndpoint;
    private final RedisTemplate redisTemplate;

    /**
     * OAuth2认证，登录入口
     * Basic aWZhbnMtYXV0aDppZmFucy1hdXRoLXNpZ25pbmc=
     */
    @PostMapping("/token")
    public Object postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        /**
         * 获取登录认证的客户端ID
         * 放在请求头（Request Headers）中的Authorization字段，且经过加密，例如 Basic Y2xpZW50OnNlY3JldA== 明文等于 client:secret
         */
        String clientId = RequestUtils.getClientId();
        log.info("OAuth认证授权 客户端ID:{}，请求参数：{}", clientId, JSONUtil.toJsonStr(parameters));

        OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        return R.ok(accessToken);
    }

    private final KeyPair keyPair;

    /**
     * 获取公钥
     */
    @GetMapping("/publicKey")
    public Map<String, Object> getPublicKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }
}
