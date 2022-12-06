package com.ifans.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.order.domain.StoreOrderPaymentInfo;

public interface PaymentInfoService extends IService<StoreOrderPaymentInfo> {

    StoreOrderPaymentInfo findByOrderNo(String orderNo);
}
