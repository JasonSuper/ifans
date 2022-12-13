package com.ifans.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.order.domain.StoreOrderPaymentInfo;
import com.ifans.api.order.domain.StoreOrderRefundinfo;

public interface RefundinfoMapper extends BaseMapper<StoreOrderRefundinfo> {

    StoreOrderRefundinfo findByOrderNo(String orderNo);
}
