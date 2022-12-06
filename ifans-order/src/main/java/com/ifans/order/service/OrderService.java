package com.ifans.order.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.api.order.domain.StoreOrderItem;
import com.ifans.api.order.vo.StoreOrderVo;
import com.ifans.order.vo.CreateOrderVo;
import com.ifans.order.vo.PayAsyncVo;

import java.util.List;

public interface OrderService extends IService<StoreOrder> {

    /**
     * 关闭订单
     * or
     * 订单逾期未付款，执行关闭处理
     */
    void orderCloseHandle(String msg) throws AlipayApiException;

    StoreOrder findByOrderNo(String orderNo);

    /**
     * 初始化订单
     */
    String initOrder(CreateOrderVo createOrderVo);

    /**
     * 获取最新的，具有相同购买属性（道具id，购买数量）的订单
     */
    StoreOrder searchLastExistOrder(CreateOrderVo createOrderVo);

    IPage<StoreOrderVo> pageList(Page page, int status);

    /**
     * 支付成功回调 订单处理
     */
    String handlePayResult(PayAsyncVo vo);

    void cleanOrderRedis(String id, String userid);
}
