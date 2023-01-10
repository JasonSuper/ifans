package com.ifans.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.order.domain.StoreOrderRefundapply;
import com.ifans.api.order.domain.StoreOrderRefundinfo;

import java.util.List;

/**
 * 退款申请Service接口
 *
 * @author ifans
 * @date 2023-01-06
 */
public interface StoreOrderRefundapplyService extends IService<StoreOrderRefundapply> {
    /**
     * 查询退款申请
     *
     * @param id 退款申请主键
     * @return 退款申请
     */
    StoreOrderRefundapply selectStoreOrderRefundapplyById(String id);

    /**
     * 查询退款申请列表
     *
     * @param storeOrderRefundapply 退款申请
     * @return 退款申请集合
     */
    List<StoreOrderRefundapply> selectStoreOrderRefundapplyList(StoreOrderRefundapply storeOrderRefundapply);

    /**
     * 新增退款申请
     *
     * @param storeOrderRefundapply 退款申请
     * @return 结果
     */
    int insertStoreOrderRefundapply(StoreOrderRefundapply storeOrderRefundapply);

    /**
     * 修改退款申请
     *
     * @param storeOrderRefundapply 退款申请
     * @return 结果
     */
    int updateStoreOrderRefundapply(StoreOrderRefundapply storeOrderRefundapply);

    /**
     * 批量删除退款申请
     *
     * @param ids 需要删除的退款申请主键集合
     * @return 结果
     */
    int deleteStoreOrderRefundapplyByIds(String[] ids);

    /**
     * 删除退款申请信息
     *
     * @param id 退款申请主键
     * @return 结果
     */
    int deleteStoreOrderRefundapplyById(String id);
}
