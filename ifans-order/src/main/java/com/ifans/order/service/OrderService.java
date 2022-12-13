package com.ifans.order.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.api.order.vo.StoreOrderVo;
import com.ifans.order.vo.CreateOrderVo;
import com.ifans.order.vo.AliPayAsyncVo;
import com.ifans.order.vo.YzfPayAsyncVo;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
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

    IPage<StoreOrderVo> pageList(IPage<?> page, int status);

    /**
     * 支付成功回调 订单处理
     */
    String handlePayResult(AliPayAsyncVo vo);

    //String handlePayResult(YzfPayAsyncVo vo);

    void orderHandle(String paytimeStr, String payPrice, StoreOrder storeOrder) throws ParseException;

    void cleanOrderRedis(String id, String userid);

    /**
     * 获取超过5分钟未支付的订单
     */
    List<StoreOrder> getNotPayOrder();
}
