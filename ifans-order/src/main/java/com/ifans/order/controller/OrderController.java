package com.ifans.order.controller;

import com.alipay.api.AlipayApiException;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.api.snowflake.FeignSnowFlake;
import com.ifans.common.core.domain.R;
import com.ifans.order.conf.AlipayTemplate;
import com.ifans.order.service.OrderService;
import com.ifans.order.vo.CreateOrderVo;
import com.ifans.order.vo.PayVo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private AlipayTemplate alipayTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FeignSnowFlake feignSnowFlake;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @PostMapping("/createOrder")
    public R createOrder(CreateOrderVo createOrderVo) {
        StoreOrder storeOrder = orderService.createOrder(createOrderVo);
        Message message = MessageBuilder.withPayload(storeOrder.getOrderNo().getBytes(StandardCharsets.UTF_8)).build();
        // 默认值为“1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h”，18个level
        rocketMQTemplate.syncSend("order-close-topic", message, 30000, 14); // 发送延迟消息

        R r = feignSnowFlake.getSnowFlakeId();
        String token = ((Map) r.getData()).get("id").toString();
        Map<String, String> result = new HashMap<>();
        result.put("token", token);

        // 缓存到redis，用于后续的幂等性操作
        redisTemplate.opsForValue().set(token, storeOrder.getId(), 30, TimeUnit.MINUTES);
        return R.ok(result);
    }

    @GetMapping(value = "/pay", produces = "text/html")
    public String pay(String orderNo) throws AlipayApiException {
        StoreOrder order = orderService.findByOrderNo(orderNo);
        if (order != null) {
            // 根据订单号到数据库查询订单消息，这里偷懒，直接赋值
            PayVo payVo = new PayVo();
            payVo.setOut_trade_no(orderNo); // 商户订单号
            payVo.setSubject("手机秒杀订单"); // 订单名称
            payVo.setTotal_amount("999"); // 付款金额
            payVo.setBody(""); //商品描述 可空

            String payPage = alipayTemplate.pay(payVo);
            return payPage;
        }
        return null;
    }

    @GetMapping(value = "/closepay")
    public boolean closepay(String orderSn) throws AlipayApiException {
        return alipayTemplate.closepay(orderSn);
    }
}
