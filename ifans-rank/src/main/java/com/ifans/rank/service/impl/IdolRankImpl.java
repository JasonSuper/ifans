package com.ifans.rank.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.rank.domain.IdolRank;
import com.ifans.rank.mapper.IdolRankMapper;
import com.ifans.rank.service.IdolRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdolRankImpl extends ServiceImpl<IdolRankMapper, IdolRank> implements IdolRankService {

    @Autowired
    private IdolRankMapper idolRankMapper;

    @Override
    public IPage<IdolRank> pageList(IPage<?> page, Integer status) {
        return idolRankMapper.pageList(page, status);
    }
}
