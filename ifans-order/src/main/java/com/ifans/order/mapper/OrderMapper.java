package com.ifans.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.order.domain.StoreOrder;

public interface OrderMapper extends BaseMapper<StoreOrder> {

    StoreOrder selectByOrderNo(String orderSn);

    int updateOrderStatus(String orderNo, String statusCode);
}
