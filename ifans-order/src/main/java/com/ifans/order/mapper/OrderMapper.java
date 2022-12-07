package com.ifans.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.api.order.domain.StoreOrderItem;
import com.ifans.api.order.vo.StoreOrderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper extends BaseMapper<StoreOrder> {

    StoreOrder selectByOrderNo(String orderSn);

    IPage<StoreOrderVo> pageList(Page page, @Param("status") int status, @Param("userId") String userId);

    List<StoreOrderItem> searchOrderItemsByOrderId(String id);

    List<StoreOrder> getNotPayOrder();
}
