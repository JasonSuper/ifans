package com.ifans.rank.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.rank.domain.IdolRank;
import com.ifans.rank.mapper.IdolRankMapper;
import com.ifans.rank.mapper.StoreGoodsScoreMapper;
import com.ifans.rank.mapper.UserGoodsBagMapper;
import com.ifans.rank.service.IdolRankService;
import com.ifans.rank.vo.HitCallVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class IdolRankImpl extends ServiceImpl<IdolRankMapper, IdolRank> implements IdolRankService {

    @Autowired
    private IdolRankMapper idolRankMapper;
    @Autowired
    private UserGoodsBagMapper userGoodsBagMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StoreGoodsScoreMapper storeGoodsScoreMapper;

    @Override
    public IPage<IdolRank> pageList(IPage<?> page, Integer status) {
        return idolRankMapper.pageList(page, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int hitCall(HitCallVo hitCallVo) {
        // 使用cas更新用户道具余额
        int i = userGoodsBagMapper.updateUserGoodsTotal(hitCallVo);
        if (i > 0) { // 明星热度可以忍受适当并发问题，用户没人知道实际的真实热度
            // 获取道具的热度值
            int rankHot = giveMeGoodsRankHot(hitCallVo.getGoodsId());
            // 修改明星热度值
            idolRankMapper.upRankHot(hitCallVo.getIdolId(), rankHot);
        }
        return i;
    }

    /**
     * 获取道具的热度值
     */
    @Override
    public int giveMeGoodsRankHot(String goodsId) {
        String cacheKey = "rank_hot:" + goodsId;
        String cacheLockKey = "rank_hot_lock:" + goodsId;
        Object cache = redisTemplate.opsForValue().get(cacheKey);
        if (cache == null) {
            RLock rLock = redissonClient.getLock(cacheLockKey);
            rLock.lock(20, TimeUnit.SECONDS);
            try {
                cache = redisTemplate.opsForValue().get(cacheKey);
                if (cache == null) {
                    int rankHot = storeGoodsScoreMapper.giveMeGoodsRankHot(goodsId);
                    redisTemplate.opsForValue().set(cacheKey, rankHot, 120, TimeUnit.SECONDS);
                    return rankHot;
                } else {
                    return Integer.parseInt(cache.toString());
                }
            } finally {
                rLock.unlock();
            }
        } else {
            return Integer.parseInt(cache.toString());
        }
    }
}
