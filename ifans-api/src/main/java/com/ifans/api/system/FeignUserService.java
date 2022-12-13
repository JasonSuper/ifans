package com.ifans.api.system;

import com.ifans.api.system.domain.SysUser;
import com.ifans.api.system.domain.UserGoodsBag;
import com.ifans.api.system.fallback.FeignUserFallbackFactory;
import com.ifans.api.system.model.LoginUser;
import com.ifans.common.core.constant.SecurityConstants;
import com.ifans.common.core.domain.R;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "ifans-system", fallbackFactory = FeignUserFallbackFactory.class)
public interface FeignUserService {

    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source   请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    R<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source  请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 获取当前用户道具
     */
    @GetMapping("/userGoods/bag/{userId}")
    R<UserGoodsBag> bag(@PathVariable("userId") String userId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 添加当前用户道具
     */
    @PostMapping("/userGoods/add")
    R add(@RequestBody UserGoodsBag userGoodsBag, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
