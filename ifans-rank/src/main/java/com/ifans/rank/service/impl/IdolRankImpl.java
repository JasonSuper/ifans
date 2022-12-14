package com.ifans.rank.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.common.core.utils.SecurityUtils;
import com.ifans.rank.domain.IdolRank;
import com.ifans.rank.mapper.IdolRankMapper;
import com.ifans.rank.service.IdolRankService;
import com.ifans.rank.vo.HitCallVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class IdolRankImpl extends ServiceImpl<IdolRankMapper, IdolRank> implements IdolRankService {

    @Autowired
    private IdolRankMapper idolRankMapper;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public IPage<IdolRank> pageList(IPage<?> page, Integer status) {
        return idolRankMapper.pageList(page, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean hitCall(HitCallVo hitCallVo) {
        RLock lock = redissonClient.getLock("hitCall_" + SecurityUtils.getUserId());
        lock.lock(20, TimeUnit.SECONDS); // 串行化使用道具，每个用户不能并发使用道具

        try {

        } finally {
            lock.unlock();
        }
        return false;
    }
}
