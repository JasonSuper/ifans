package com.ifans.api.rank;

import com.ifans.api.rank.domain.UserGoodsBagTurnover;
import com.ifans.api.rank.vo.UserGoodsBagVo;
import com.ifans.api.system.fallback.FeignUserFallbackFactory;
import com.ifans.common.core.constant.SecurityConstants;
import com.ifans.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(value = "ifans-rank", fallbackFactory = FeignUserFallbackFactory.class)
public interface FeignRankService {

    /**
     * 获取当前用户道具
     */
    @GetMapping("/userGoods/bag/{userId}")
    R<UserGoodsBagVo> bag(@PathVariable("userId") String userId);

    /**
     * 添加当前用户道具
     */
    @PostMapping("/userGoods/add")
    R add(@RequestBody UserGoodsBagTurnover userGoodsBagTurnover);
}
