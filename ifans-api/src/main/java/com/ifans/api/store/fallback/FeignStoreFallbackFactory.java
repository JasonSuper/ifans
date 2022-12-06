package com.ifans.api.store.fallback;

import com.ifans.api.store.FeignStoreService;
import com.ifans.common.core.web.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignStoreFallbackFactory implements FallbackFactory<FeignStoreService> {
    private static final Logger log = LoggerFactory.getLogger(FeignStoreFallbackFactory.class);

    @Override
    public FeignStoreService create(Throwable throwable) {
        log.error("雪花ID服务调用失败:{}", throwable.getMessage());
        return new FeignStoreService() {
            @Override
            public AjaxResult info(String id) {
                return AjaxResult.error("获取商品详细信息失败:" + throwable.getMessage());
            }
        };
    }
}