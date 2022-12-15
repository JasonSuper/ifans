package com.ifans.rank.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.store.domain.StoreGoodsScore;
import com.ifans.rank.mapper.StoreGoodsScoreMapper;
import com.ifans.rank.service.StoreGoodsScoreService;
import org.springframework.stereotype.Service;

@Service
public class StoreGoodsScoreServiceImpl extends ServiceImpl<StoreGoodsScoreMapper, StoreGoodsScore> implements StoreGoodsScoreService {
}
