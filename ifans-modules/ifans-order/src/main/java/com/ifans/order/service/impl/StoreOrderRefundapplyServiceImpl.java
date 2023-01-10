package com.ifans.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.order.domain.StoreOrderRefundapply;
import com.ifans.api.order.domain.StoreOrderRefundinfo;
import com.ifans.common.core.util.DateUtils;
import com.ifans.order.mapper.RefundinfoMapper;
import com.ifans.order.mapper.StoreOrderRefundapplyMapper;
import com.ifans.order.service.StoreOrderRefundapplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 退款申请Service业务层处理
 *
 * @author ifans
 * @date 2023-01-06
 */
@Service
public class StoreOrderRefundapplyServiceImpl extends ServiceImpl<StoreOrderRefundapplyMapper, StoreOrderRefundapply> implements StoreOrderRefundapplyService {
    @Autowired
    private StoreOrderRefundapplyMapper storeOrderRefundapplyMapper;

    /**
     * 查询退款申请
     *
     * @param id 退款申请主键
     * @return 退款申请
     */
    @Override
    public StoreOrderRefundapply selectStoreOrderRefundapplyById(String id) {
        return storeOrderRefundapplyMapper.selectStoreOrderRefundapplyById(id);
    }

    /**
     * 查询退款申请列表
     *
     * @param storeOrderRefundapply 退款申请
     * @return 退款申请
     */
    @Override
    public List<StoreOrderRefundapply> selectStoreOrderRefundapplyList(StoreOrderRefundapply storeOrderRefundapply) {
        return storeOrderRefundapplyMapper.selectStoreOrderRefundapplyList(storeOrderRefundapply);
    }

    /**
     * 新增退款申请
     *
     * @param storeOrderRefundapply 退款申请
     * @return 结果
     */
    @Override
    public int insertStoreOrderRefundapply(StoreOrderRefundapply storeOrderRefundapply) {
        storeOrderRefundapply.setCreateTime(DateUtils.getNowDate());
        return storeOrderRefundapplyMapper.insertStoreOrderRefundapply(storeOrderRefundapply);
    }

    /**
     * 修改退款申请
     *
     * @param storeOrderRefundapply 退款申请
     * @return 结果
     */
    @Override
    public int updateStoreOrderRefundapply(StoreOrderRefundapply storeOrderRefundapply) {
        storeOrderRefundapply.setUpdateTime(DateUtils.getNowDate());
        return storeOrderRefundapplyMapper.updateStoreOrderRefundapply(storeOrderRefundapply);
    }

    /**
     * 批量删除退款申请
     *
     * @param ids 需要删除的退款申请主键
     * @return 结果
     */
    @Override
    public int deleteStoreOrderRefundapplyByIds(String[] ids) {
        return storeOrderRefundapplyMapper.deleteStoreOrderRefundapplyByIds(ids);
    }

    /**
     * 删除退款申请信息
     *
     * @param id 退款申请主键
     * @return 结果
     */
    @Override
    public int deleteStoreOrderRefundapplyById(String id) {
        return storeOrderRefundapplyMapper.deleteStoreOrderRefundapplyById(id);
    }
}
