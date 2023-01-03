package com.ifans.api.rank.fallback;

import com.ifans.api.rank.FeignRankService;
import com.ifans.api.rank.domain.UserGoodsBagTurnover;
import com.ifans.api.rank.vo.UserGoodsBagVo;
import com.ifans.common.core.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignRankFallbackFactory implements FallbackFactory<FeignRankService> {
    private static final Logger log = LoggerFactory.getLogger(FeignRankFallbackFactory.class);

    @Override
    public FeignRankService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new FeignRankService() {

            @Override
            public R<UserGoodsBagVo> bag(String userId) {
                System.out.println("获取用户背包失败：" + throwable.getMessage());
                return R.failed(null);
            }

            @Override
            public R add(UserGoodsBagTurnover userGoodsBagTurnover) {
                return R.failed("添加当前用户道具失败");
            }
        };
    }
}