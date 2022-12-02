package com.ifans.api.snowflake.fallback;

import com.ifans.api.snowflake.FeignSnowFlake;
import com.ifans.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignSnowFlakeFallbackFactory implements FallbackFactory<FeignSnowFlake> {
    private static final Logger log = LoggerFactory.getLogger(FeignSnowFlakeFallbackFactory.class);

    @Override
    public FeignSnowFlake create(Throwable throwable) {
        log.error("雪花ID服务调用失败:{}", throwable.getMessage());
        return new FeignSnowFlake() {
            @Override
            public R getSnowFlakeId() {
                return R.fail("获取雪花ID失败:" + throwable.getMessage());
            }
        };
    }
}