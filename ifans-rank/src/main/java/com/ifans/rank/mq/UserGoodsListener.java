package com.ifans.rank.mq;

import com.alibaba.fastjson2.JSON;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.api.order.domain.StoreOrderItem;
import com.ifans.api.order.vo.StoreOrderVo;
import com.ifans.api.rank.domain.UserGoodsBagTurnover;
import com.ifans.common.core.constant.SecurityConstants;
import com.ifans.rank.service.UserGoodsBagService;
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

import java.util.Date;
import java.util.List;

/**
 * 监听订单付款成功后，用户道具添加消息
 * 将道具添加到用户背包
 */
@RocketMQMessageListener(
        consumerGroup = "rank-bag-consumer",
        topic = "rank-bag-topic",
        consumeMode = ConsumeMode.CONCURRENTLY, // 并发模式
        messageModel = MessageModel.CLUSTERING // 集群模式，如果有多个消费者，则平摊消费消息
)
@Component
public class UserGoodsListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private UserGoodsBagService userGoodsBagService;

    @Override
    public void onMessage(MessageExt message) {
        System.out.println("------- 用户道具添加，msgid：" + message.getMsgId() + "，订单号：" + new String(message.getBody()));
        // 订单复查，检测订单是否已付款
        try {
            StoreOrderVo storeOrder = JSON.parseObject(new String(message.getBody()), StoreOrderVo.class);
            userGoodsBagService.addGoodsForUser(storeOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        // 设置重试次数
        consumer.setMaxReconsumeTimes(3);
        // 一个新的订阅组第一次启动从队列的最后位置开始消费，后续再启动接着上次消费的进度开始消费（判断是不是一个新的ConsumerGroup是在broker端判断）
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 设置负载均衡策略（只对集群模式生效），默认就是轮询策略(AllocateMessageQueueAveragely)
        consumer.setAllocateMessageQueueStrategy(new AllocateMessageQueueAveragely());
    }
}