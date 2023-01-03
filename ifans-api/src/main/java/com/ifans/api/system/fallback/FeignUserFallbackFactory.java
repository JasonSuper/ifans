package com.ifans.api.system.fallback;

import com.ifans.api.system.FeignUserService;
import com.ifans.api.system.domain.SysUser;
import com.ifans.api.system.model.LoginUser;
import com.ifans.common.core.util.R;
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
            public R<LoginUser> getUserInfo(String username) {
                return R.failed("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> registerUserInfo(SysUser sysUser) {
                return R.failed("注册用户失败:" + throwable.getMessage());
            }
        };
    }
}