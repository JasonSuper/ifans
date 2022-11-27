package com.ifans.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ifans.api.system.domain.StoreGoods;
import org.apache.ibatis.annotations.Param;

public interface StoreMapper extends BaseMapper<StoreGoods> {

    IPage<StoreGoods> pageList(IPage<?> page, @Param("status") Integer status);
}
