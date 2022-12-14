package com.ifans.rank.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.store.domain.StoreGoods;
import com.ifans.rank.domain.IdolRank;
import com.ifans.rank.vo.HitCallVo;

public interface IdolRankService extends IService<IdolRank> {

    IPage<IdolRank> pageList(IPage<?> page, Integer status);

    boolean hitCall(HitCallVo hitCallVo);
}
