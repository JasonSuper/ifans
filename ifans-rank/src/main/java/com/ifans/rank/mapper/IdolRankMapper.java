package com.ifans.rank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ifans.api.store.domain.StoreGoods;
import com.ifans.rank.domain.IdolRank;

public interface IdolRankMapper extends BaseMapper<IdolRank> {

    IPage<IdolRank> pageList(IPage<?> page, Integer status);
}
