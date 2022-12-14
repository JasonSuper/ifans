package com.ifans.rank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.order.vo.StoreOrderVo;
import com.ifans.api.rank.domain.UserGoodsBag;
import com.ifans.api.rank.domain.UserGoodsBagTurnover;
import com.ifans.api.rank.vo.UserGoodsBagVo;

import java.util.List;

public interface UserGoodsBagService extends IService<UserGoodsBag> {

    List<UserGoodsBagVo> selectByUserId(String userid);

    void addGoodsForUser(UserGoodsBagTurnover turnover);

    void addGoodsForUser(StoreOrderVo storeOrderVo);
}
