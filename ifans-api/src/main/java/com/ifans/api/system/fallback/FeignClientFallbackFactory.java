package com.ifans.api.system.fallback;

import com.ifans.api.system.FeignClientDetailsService;
import com.ifans.api.system.domain.SysOauthClientDetails;
import com.ifans.common.core.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeignClientFallbackFactory implements FallbackFactory<FeignClientDetailsService> {
    private static final Logger log = LoggerFactory.getLogger(FeignUserFallbackFactory.class);

    @Override
    public FeignClientDetailsService create(Throwable throwable) {
        log.error("客户端信息调用失败:{}", throwable.getMessage());
        return new FeignClientDetailsService() {
            @Override
            public R<SysOauthClientDetails> getClientDetailsById(String clientId) {
                return R.failed("获取客户端信息失败:" + throwable.getMessage());
            }

            @Override
            public R<List<SysOauthClientDetails>> listClientDetails() {
                return R.failed("获取全部客户端失败:" + throwable.getMessage());
            }
        };
    }
}
