package com.ifans.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.system.domain.StoreGoods;

/**
 * 道具信息 业务层
 */
public interface StoreService extends IService<StoreGoods> {

    IPage<StoreGoods> pageList(IPage<?> page, Integer status);
}