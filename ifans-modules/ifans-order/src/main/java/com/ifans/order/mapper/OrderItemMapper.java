package com.ifans.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ifans.api.order.domain.StoreOrderItem;

import java.util.List;

public interface OrderItemMapper extends BaseMapper<StoreOrderItem> {


    List<StoreOrderItem> selectByOrderNo(String order_no);
}
