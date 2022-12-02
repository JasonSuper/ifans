package com.ifans.order.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.api.order.domain.StoreOrderItem;
import com.ifans.api.snowflake.FeignSnowFlake;
import com.ifans.api.store.FeignStoreService;
import com.ifans.api.store.domain.StoreGoods;
import com.ifans.common.core.domain.R;
import com.ifans.common.core.utils.SecurityUtils;
import com.ifans.common.core.web.domain.AjaxResult;
import com.ifans.order.conf.AlipayTemplate;
import com.ifans.order.enums.OrderStatusEnum;
import com.ifans.order.mapper.OrderItemMapper;
import com.ifans.order.mapper.OrderMapper;
import com.ifans.order.service.OrderService;
import com.ifans.order.vo.CreateOrderVo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, StoreOrder> implements OrderService {

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private FeignSnowFlake feignSnowFlake;

    @Autowired
    private FeignStoreService feignStoreService;

    /**
     * 订单逾期未付款，执行关闭处理
     *
     * @param msg 订单信息
     */
    @Override
    public void orderCloseHandle(String msg) throws AlipayApiException {
        String orderNo = msg;

        RLock rLock = redissonClient.getLock(orderNo);
        rLock.lock(10, TimeUnit.SECONDS);

        // 对订单号加锁，防止并发的支付问题
        try {
            // 查询订单状态
            StoreOrder oe = orderMapper.selectByOrderNo(orderNo);

            if (oe == null) {
                System.out.println("未找到订单：" + orderNo);
                return;
            }

            // 如果订单未付款，则设置为取消状态，关闭订单
            if (OrderStatusEnum.PAYED.getCode() > oe.getPayStatus()) {
                // 修改订单状态
                orderMapper.updateOrderStatus(orderNo, OrderStatusEnum.CANCLED.getCode().toString());
                // 关闭支付宝交易
                alipayTemplate.closepay(orderNo);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw e;
        } finally {
            rLock.unlock();
        }
    }

    @Transactional
    @Override
    public StoreOrder createOrder(CreateOrderVo createOrderVo) {
        // 获取商品信息
        AjaxResult storeGoodsResult = feignStoreService.info(createOrderVo.getGoodsId());
        StoreGoods storeGoods = JSON.parseObject(storeGoodsResult.get("data").toString(), StoreGoods.class);

        R r = feignSnowFlake.getSnowFlakeId();
        String snowid = ((Map) r.getData()).get("id").toString();

        // 创建订单
        StoreOrder storeOrder = new StoreOrder();
        storeOrder.setOrderNo(snowid);
        storeOrder.setOrderTime(new Date(createOrderVo.getTimestamp()));
        storeOrder.setUserId(SecurityUtils.getUserId());
        storeOrder.setNum(createOrderVo.getCount());
        storeOrder.setOrderPrice(new BigDecimal(createOrderVo.getPay_money()));
        storeOrder.setMustPrice(new BigDecimal(createOrderVo.getPay_money()));
        storeOrder.setPayStatus(OrderStatusEnum.CREATE_NEW.getCode());
        storeOrder.setStatus("0");
        int i = orderMapper.insert(storeOrder);

        // 创建订单详细项
        StoreOrderItem storeOrderItem = new StoreOrderItem();
        storeOrderItem.setOrderId(storeOrder.getId());
        storeOrderItem.setOrderNo(storeOrder.getOrderNo());
        storeOrderItem.setGoodsId(createOrderVo.getGoodsId());
        storeOrderItem.setGoodsName(storeGoods.getGoodsName());
        storeOrderItem.setGoodsIcon(storeGoods.getIcon());
        storeOrderItem.setNum(createOrderVo.getCount());

        int j = orderItemMapper.insert(storeOrderItem);
        return storeOrder;
    }

    @Override
    public StoreOrder findByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }
}
