package com.ifans.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.order.domain.StoreOrderRefundinfo;

public interface RefundinfoService extends IService<StoreOrderRefundinfo> {

    StoreOrderRefundinfo findByOrderNo(String orderNo);
}
