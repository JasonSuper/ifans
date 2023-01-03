package com.ifans.api.snowflake;

import com.ifans.api.snowflake.fallback.FeignSnowFlakeFallbackFactory;
import com.ifans.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "ifans-snowflake", fallbackFactory = FeignSnowFlakeFallbackFactory.class)
public interface FeignSnowFlake {

    @GetMapping("/getSnowFlakeId")
    R getSnowFlakeId();
}
