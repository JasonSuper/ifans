package com.ifans.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.order.domain.StoreOrderPaymentInfo;
import com.ifans.api.order.domain.StoreOrderRefundapply;

import java.util.List;

/**
 * 退款申请Mapper接口
 *
 * @author ifans
 * @date 2023-01-06
 */
public interface StoreOrderRefundapplyMapper extends BaseMapper<StoreOrderRefundapply> {
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
     * 删除退款申请
     *
     * @param id 退款申请主键
     * @return 结果
     */
    int deleteStoreOrderRefundapplyById(String id);

    /**
     * 批量删除退款申请
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteStoreOrderRefundapplyByIds(String[] ids);

    String getInstanceId(String refundApplyId);
}
