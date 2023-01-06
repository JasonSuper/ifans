package com.ifans.system.service.impl;

import com.ifans.api.store.domain.StoreGoods;
import com.ifans.common.core.util.DateUtils;
import com.ifans.system.mapper.StoreGoodsMapper;
import com.ifans.system.service.IStoreGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 道具信息Service业务层处理
 *
 * @author ifans
 * @date 2023-01-06
 */
@Service
public class StoreGoodsServiceImpl implements IStoreGoodsService {
    @Autowired
    private StoreGoodsMapper storeGoodsMapper;

    /**
     * 查询道具信息
     *
     * @param id 道具信息主键
     * @return 道具信息
     */
    @Override
    public StoreGoods selectStoreGoodsById(String id) {
        return storeGoodsMapper.selectStoreGoodsById(id);
    }

    /**
     * 查询道具信息列表
     *
     * @param storeGoods 道具信息
     * @return 道具信息
     */
    @Override
    public List<StoreGoods> selectStoreGoodsList(StoreGoods storeGoods) {
        return storeGoodsMapper.selectStoreGoodsList(storeGoods);
    }

    /**
     * 新增道具信息
     *
     * @param storeGoods 道具信息
     * @return 结果
     */
    @Override
    public int insertStoreGoods(StoreGoods storeGoods) {
        storeGoods.setCreateTime(DateUtils.getNowDate());
        return storeGoodsMapper.insertStoreGoods(storeGoods);
    }

    /**
     * 修改道具信息
     *
     * @param storeGoods 道具信息
     * @return 结果
     */
    @Override
    public int updateStoreGoods(StoreGoods storeGoods) {
        storeGoods.setUpdateTime(DateUtils.getNowDate());
        return storeGoodsMapper.updateStoreGoods(storeGoods);
    }

    /**
     * 批量删除道具信息
     *
     * @param ids 需要删除的道具信息主键
     * @return 结果
     */
    @Override
    public int deleteStoreGoodsByIds(String[] ids) {
        return storeGoodsMapper.deleteStoreGoodsByIds(ids);
    }

    /**
     * 删除道具信息信息
     *
     * @param id 道具信息主键
     * @return 结果
     */
    @Override
    public int deleteStoreGoodsById(String id) {
        return storeGoodsMapper.deleteStoreGoodsById(id);
    }
}
