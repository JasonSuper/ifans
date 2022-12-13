package com.ifans.api.system.fallback;

import com.ifans.api.system.FeignUserService;
import com.ifans.api.system.domain.SysUser;
import com.ifans.api.system.domain.UserGoodsBag;
import com.ifans.api.system.model.LoginUser;
import com.ifans.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignUserFallbackFactory implements FallbackFactory<FeignUserService> {
    private static final Logger log = LoggerFactory.getLogger(FeignUserFallbackFactory.class);

    @Override
    public FeignUserService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new FeignUserService() {
            @Override
            public R<LoginUser> getUserInfo(String username, String source) {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> registerUserInfo(SysUser sysUser, String source) {
                return R.fail("注册用户失败:" + throwable.getMessage());
            }

            @Override
            public R<UserGoodsBag> bag(String userId, String source) {
                System.out.println("获取用户背包失败：" + throwable.getMessage());
                return R.fail(null);
            }

            @Override
            public R add(UserGoodsBag userGoodsBag, String source) {
                return R.fail("添加当前用户道具失败");
            }
        };
    }
}