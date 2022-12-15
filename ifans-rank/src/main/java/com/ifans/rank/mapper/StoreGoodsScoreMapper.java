package com.ifans.rank.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.store.domain.StoreGoodsScore;

public interface StoreGoodsScoreMapper extends BaseMapper<StoreGoodsScore> {

    int giveMeGoodsRankHot(String goodsId);
}
