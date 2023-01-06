package com.ifans.system.mapper;

import com.ifans.api.store.domain.StoreGoods;

import java.util.List;

/**
 * 道具信息Mapper接口
 *
 * @author ifans
 * @date 2023-01-06
 */
public interface StoreGoodsMapper {
    /**
     * 查询道具信息
     *
     * @param id 道具信息主键
     * @return 道具信息
     */
    public StoreGoods selectStoreGoodsById(String id);

    /**
     * 查询道具信息列表
     *
     * @param storeGoods 道具信息
     * @return 道具信息集合
     */
    public List<StoreGoods> selectStoreGoodsList(StoreGoods storeGoods);

    /**
     * 新增道具信息
     *
     * @param storeGoods 道具信息
     * @return 结果
     */
    public int insertStoreGoods(StoreGoods storeGoods);

    /**
     * 修改道具信息
     *
     * @param storeGoods 道具信息
     * @return 结果
     */
    public int updateStoreGoods(StoreGoods storeGoods);

    /**
     * 删除道具信息
     *
     * @param id 道具信息主键
     * @return 结果
     */
    public int deleteStoreGoodsById(String id);

    /**
     * 批量删除道具信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStoreGoodsByIds(String[] ids);
}
