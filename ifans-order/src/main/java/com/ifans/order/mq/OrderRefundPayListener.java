package com.ifans.order.mq;

import com.alibaba.fastjson.JSON;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.common.core.utils.StringUtils;
import com.ifans.order.enums.OrderStatusEnum;
import com.ifans.order.pay.AliPayTemplate;
import com.ifans.order.service.OrderService;
import com.ifans.order.vo.RefundPayVo;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 监听订单退款请求
 */
@RocketMQMessageListener(
        consumerGroup = "order-refundpay-consumer",
        topic = "order-refundpay-topic",
        consumeMode = ConsumeMode.CONCURRENTLY, // 并发模式
        messageModel = MessageModel.CLUSTERING // 集群模式，如果有多个消费者，则平摊消费消息
        //selectorType = SelectorType.TAG,
        //selectorExpression = "TagA || TagC",
//        selectorType = SelectorType.SQL92,
//        selectorExpression="age > 18"
)
@Component
public class OrderRefundPayListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AliPayTemplate alipayTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void onMessage(MessageExt message) {
        System.out.printf("------- 订单退款 received message, msgId: %s, body:%s \n", message.getMsgId(), new String(message.getBody()));
        // 订单退款请求
        try {
            RefundPayVo refundPayVo = JSON.parseObject(new String(message.getBody()), RefundPayVo.class);
            StoreOrder storeOrder = orderService.findByOrderNo(refundPayVo.getOut_trade_no());

            RLock lock = redissonClient.getLock(storeOrder.getOrderNo());
            lock.lock(20, TimeUnit.SECONDS); // 加锁避免并发操作，例如：在取消支付的时候，其他微服务并发进行支付、修改订单等操作
            try {
                // 退款失败如何处理
                String result = alipayTemplate.refundpay(refundPayVo);
                if (StringUtils.isNotEmpty(result)) { // 退款成功返回值不为null
                    if (storeOrder.getPayStatus() == OrderStatusEnum.CANCLED.getCode()) {

                    } else {
                        // 订单设置为已退款
                        storeOrder.setPayStatus(OrderStatusEnum.RECIEVED.getCode());
                        // 进行道具回收，等操作
                        // do something...

                        orderService.updateById(storeOrder);
                    }
                }
            } finally {
                lock.unlock();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        // 设置重试次数
        consumer.setMaxReconsumeTimes(1);
        // 一个新的订阅组第一次启动从队列的最后位置开始消费，后续再启动接着上次消费的进度开始消费（判断是不是一个新的ConsumerGroup是在broker端判断）
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 设置负载均衡策略（只对集群模式生效），默认就是轮询策略(AllocateMessageQueueAveragely)
        consumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueAveragely());
    }
}
