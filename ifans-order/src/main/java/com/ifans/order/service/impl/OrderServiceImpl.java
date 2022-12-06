package com.ifans.order.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.api.order.domain.StoreOrderItem;
import com.ifans.api.order.domain.StoreOrderPaymentInfo;
import com.ifans.api.order.vo.StoreOrderVo;
import com.ifans.api.snowflake.FeignSnowFlake;
import com.ifans.api.store.FeignStoreService;
import com.ifans.api.store.domain.StoreGoods;
import com.ifans.common.core.utils.SecurityUtils;
import com.ifans.common.core.web.domain.AjaxResult;
import com.ifans.order.conf.AlipayTemplate;
import com.ifans.order.controller.OrderController;
import com.ifans.order.enums.OrderStatusEnum;
import com.ifans.order.mapper.OrderItemMapper;
import com.ifans.order.mapper.OrderMapper;
import com.ifans.order.service.OrderService;
import com.ifans.order.service.PaymentInfoService;
import com.ifans.order.vo.CreateOrderVo;
import com.ifans.order.vo.PayAsyncVo;
import com.ifans.order.vo.RefundPayVo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, StoreOrder> implements OrderService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
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
    @Autowired
    private PaymentInfoService paymentInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String initOrder(CreateOrderVo createOrderVo) {
        // 获取商品信息
        AjaxResult storeGoodsResult = feignStoreService.info(createOrderVo.getGoodsId());
        StoreGoods storeGoods = JSON.parseObject(storeGoodsResult.get("data").toString(), StoreGoods.class);

        // 创建订单
        StoreOrder storeOrder = new StoreOrder();
        storeOrder.setOrderNo(IdWorker.getId(storeOrder) + "");
        storeOrder.setOrderTime(new Date());
        storeOrder.setUserId(SecurityUtils.getUserId());
        storeOrder.setOrderPrice(createOrderVo.getPay_money());
        storeOrder.setMustPrice(createOrderVo.getPay_money());
        storeOrder.setPayStatus(OrderStatusEnum.CREATE_NEW.getCode());
        storeOrder.setStatus("0");
        orderMapper.insert(storeOrder); // 入库

        // 创建订单详细项
        StoreOrderItem storeOrderItem = new StoreOrderItem();
        storeOrderItem.setOrderId(storeOrder.getId());
        storeOrderItem.setOrderNo(storeOrder.getOrderNo());
        storeOrderItem.setGoodsId(createOrderVo.getGoodsId());
        storeOrderItem.setGoodsName(storeGoods.getGoodsName());
        storeOrderItem.setGoodsIcon(storeGoods.getIcon());
        storeOrderItem.setNum(createOrderVo.getCount());
        orderItemMapper.insert(storeOrderItem); // 入库

        Message message = MessageBuilder.withPayload(storeOrder.getOrderNo().getBytes(StandardCharsets.UTF_8)).build();
        // 默认值为“1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h”，18个level
        rocketMQTemplate.syncSend("order-close-topic", message, 30000, 14); // 发送延迟消息

        // 生成token
        //long token = IdWorker.getId(storeOrder);

        String orderKey = OrderController.ORDER + SecurityUtils.getUserId() + ":" + createOrderVo.getGoodsId() + createOrderVo.getCount() + ":" + storeOrder.getId();
        /*// 缓存到redis，用于后续的幂等性操作
        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(key);
        //boundHashOperations.put("token", token);
        boundHashOperations.put("order", storeOrder);
        boundHashOperations.expire(30, TimeUnit.MINUTES);*/

        // 缓存到redis，用于后续的幂等性操作
        redisTemplate.opsForValue().set(orderKey, storeOrder, 30, TimeUnit.MINUTES);
        return storeOrder.getId();
    }

    /**
     * 获取最新的，具有相同购买属性（道具id，购买数量）的订单
     */
    @Override
    public StoreOrder searchLastExistOrder(CreateOrderVo createOrderVo) {
        //调用lua脚本并执行
        /*DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(String.class);//返回类型
        redisScript.setScriptText("if redis.call('exists', KEYS[1]) == 1 then return true else return false end");
        Object value = redisTemplate.execute(redisScript, Arrays.asList(key), "order");*/

        StoreOrder storeOrder = null;

        // redis模糊检索订单的key
        String searchOrderKey = OrderController.ORDER + SecurityUtils.getUserId() + ":" + createOrderVo.getGoodsId() + createOrderVo.getCount() + ":*";
        ScanOptions scanOptions = ScanOptions.scanOptions().count(100L).match(searchOrderKey).build();
        Cursor<String> cursors = redisTemplate.scan(scanOptions);
        try {
            /*cursors.forEachRemaining((storeOrder) -> {

            });*/
            while (cursors.hasNext()) {
                storeOrder = (StoreOrder) redisTemplate.opsForValue().get(cursors.next());
            }
        } finally {
            cursors.close();
        }
        return storeOrder;
    }

    @Override
    public IPage<StoreOrderVo> pageList(Page page, int status) {
        IPage<StoreOrderVo> orderVoListPage = orderMapper.pageList(page, status, SecurityUtils.getUserId());
        List<StoreOrderVo> storeOrderVoList = orderVoListPage.getRecords();
        storeOrderVoList.stream().forEach(item -> {
            List<StoreOrderItem> storeOrderItems = orderMapper.searchOrderItemsByOrderId(item.getId());
            item.setStoreOrderItems(storeOrderItems);
        });
        return orderVoListPage;
    }

    /**
     * 支付成功回调 订单处理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handlePayResult(PayAsyncVo vo) {
        String orderNo = vo.getOut_trade_no();
        RLock rLock = redissonClient.getLock(orderNo);
        rLock.lock(10, TimeUnit.SECONDS);

        // 加锁防止并发保存订单状态以及并发的支付问题
        // 支付宝的异步回调和同步回调，都会调用这个方法生成账单流水
        // 需要做好幂等性
        try {
            StoreOrderPaymentInfo payHis = paymentInfoService.findByOrderNo(orderNo);
            if (payHis == null) {
                StoreOrder storeOrder = orderMapper.selectByOrderNo(orderNo);

                // 1.保存交易流水
                StoreOrderPaymentInfo paymentInfo = new StoreOrderPaymentInfo();
                paymentInfo.setOrderId(storeOrder.getId());
                paymentInfo.setOrderNo(vo.getOut_trade_no());
                paymentInfo.setAlipayTradeNo(vo.getTrade_no());
                paymentInfo.setTotalAmount(new BigDecimal(vo.getTotal_amount()));
                paymentInfo.setPaySubject(vo.getSubject());
                paymentInfo.setPaymentStatus(vo.getTrade_status());
                paymentInfo.setCreateTime(vo.getGmt_create());
                paymentInfo.setConfirmTime(new Date());
                paymentInfo.setCallbackContent(JSON.toJSONString(vo));
                paymentInfo.setCallbackTime(vo.getNotify_time());
                paymentInfoService.save(paymentInfo);

                // 2.修改订单状态信息
                if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")) {
                    if (OrderStatusEnum.CREATE_NEW.getCode() == storeOrder.getPayStatus()) {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date paytime = sf.parse(vo.getGmt_payment());

                        storeOrder.setPayStatus(OrderStatusEnum.PAYED.getCode());
                        storeOrder.setPayPrice(new BigDecimal(vo.getBuyer_pay_amount()));
                        storeOrder.setPaymentTime(paytime);
                        storeOrder.setUpdateTime(new Date());

                        // 支付成功
                        orderMapper.updateById(storeOrder);
                        // 清除redis缓存
                        cleanOrderRedis(storeOrder.getId(), storeOrder.getUserId());

                    } else if (OrderStatusEnum.RECIEVED.getCode() != storeOrder.getPayStatus()) { // 订单已取消|已付款|已发送等，客户却付款了，启动退款流程
                        RefundPayVo refundPayVo = new RefundPayVo();
                        refundPayVo.setOut_trade_no(storeOrder.getOrderNo());
                        refundPayVo.setRefund_amount(vo.getBuyer_pay_amount());

                        // 退款请求，发送MQ
                        Message message = MessageBuilder.withPayload(JSON.toJSONString(refundPayVo)).build();
                        rocketMQTemplate.syncSend("order-refundpay-topic", message);
                    }
                }
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public void cleanOrderRedis(String id, String userid) {
        List<StoreOrderItem> storeOrderItems = orderMapper.searchOrderItemsByOrderId(id);
        List<String> itemKeys = storeOrderItems.stream()
                .map(item -> OrderController.ORDER + userid + ":" + item.getGoodsId() + item.getNum() + ":" + id)
                .collect(Collectors.toList());
        // 清除redis缓存
        redisTemplate.delete(itemKeys);
    }

    /**
     * 关闭订单
     * or
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
            StoreOrder storeOrder = orderMapper.selectByOrderNo(orderNo);

            if (storeOrder == null) {
                System.out.println("未找到订单：" + orderNo);
                return;
            }

            // 如果订单未付款，则设置为取消状态，关闭订单
            if (OrderStatusEnum.PAYED.getCode() > storeOrder.getPayStatus()) {
                storeOrder.setPayStatus(OrderStatusEnum.CANCLED.getCode());
                storeOrder.setUpdateTime(new Date());
                // 修改订单状态
                orderMapper.updateById(storeOrder);
                //orderMapper.updateOrderPayStatus(orderNo, OrderStatusEnum.CANCLED.getCode().toString());
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

    @Override
    public StoreOrder findByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }
}
