package com.ifans.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.order.domain.StoreOrderPaymentInfo;

public interface PaymentInfoMapper extends BaseMapper<StoreOrderPaymentInfo> {

    StoreOrderPaymentInfo findByOrderNo(String orderNo);
}
