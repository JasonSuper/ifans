package com.ifans.order.mq;

import com.ifans.order.service.OrderService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监听30分钟后订单的状况
 * 如果订单已付款则不进行操作
 * 否则将订单结束，返回库存
 */
@RocketMQMessageListener(
        consumerGroup = "skill-order-delay-consumer",
        topic = "order-close-topic",
        consumeMode = ConsumeMode.CONCURRENTLY, // 并发模式
        messageModel = MessageModel.CLUSTERING // 集群模式，如果有多个消费者，则平摊消费消息
        //selectorType = SelectorType.TAG,
        //selectorExpression = "TagA || TagC",
//        selectorType = SelectorType.SQL92,
//        selectorExpression="age > 18"
)
@Component
public class OrderCloseListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    // springboot rocketmq好像不太好用，缺少很多设置
    // 尝试一下原生API

    @Autowired
    private OrderService orderService;

    @Override
    public void onMessage(MessageExt message) {
        System.out.printf("------- 订单关闭 received message, msgId: %s, body:%s \n", message.getMsgId(), new String(message.getBody()));
        // 订单复查，检测订单是否已付款
        try {
            orderService.orderCloseHandle(new String(message.getBody()));
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
