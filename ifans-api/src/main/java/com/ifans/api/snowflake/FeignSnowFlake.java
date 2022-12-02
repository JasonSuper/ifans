package com.ifans.api.snowflake;

import com.ifans.api.snowflake.fallback.FeignSnowFlakeFallbackFactory;
import com.ifans.api.system.domain.SysUser;
import com.ifans.api.system.model.LoginUser;
import com.ifans.common.core.constant.SecurityConstants;
import com.ifans.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "ifans-snowflake", fallbackFactory = FeignSnowFlakeFallbackFactory.class)
public interface FeignSnowFlake {

    @GetMapping("/getSnowFlakeId")
    R getSnowFlakeId();
}
