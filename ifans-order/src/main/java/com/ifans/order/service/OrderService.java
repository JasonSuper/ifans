package com.ifans.order.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.order.vo.CreateOrderVo;

public interface OrderService extends IService<StoreOrder> {

    void orderCloseHandle(String msg) throws AlipayApiException;

    StoreOrder createOrder(CreateOrderVo createOrderVo);

    StoreOrder findByOrderNo(String orderNo);
}
