package com.ifans.store.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.store.domain.StoreGoods;
import com.ifans.store.mapper.StoreMapper;
import com.ifans.store.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, StoreGoods> implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

    @Override
    public IPage<StoreGoods> pageList(IPage<?> page, Integer status) {
        return storeMapper.pageList(page, status);
    }
}
