package com.ifans.api.store;

import com.ifans.api.system.fallback.FeignUserFallbackFactory;
import com.ifans.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "ifans-store", fallbackFactory = FeignUserFallbackFactory.class)
public interface FeignStoreService {

    @GetMapping("/goods/info/{id}")
    R info(@PathVariable("id") String id);
}
