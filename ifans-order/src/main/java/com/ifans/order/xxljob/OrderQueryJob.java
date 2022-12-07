package com.ifans.order.xxljob;

import com.alibaba.fastjson2.JSONObject;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.common.core.utils.StringUtils;
import com.ifans.order.pay.AliPayTemplate;
import com.ifans.order.service.OrderService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 定时查单
 */
@Component
public class OrderQueryJob {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AliPayTemplate alipayTemplate;
    @Autowired
    private RedissonClient redissonClient;

    Executor executor = Executors.newCachedThreadPool();

    /**
     * 统一收单交易查询
     */
    @XxlJob("orderQueryJob")
    public void orderQueryJob() {
        //System.out.println("定时查单执行");
        List<StoreOrder> orderList = orderService.getNotPayOrder();
        orderList.stream().forEach(order -> {
            executor.execute(() -> {
                RLock lock = redissonClient.getLock(order.getOrderNo());
                lock.lock(20, TimeUnit.SECONDS);
                try {
                    System.out.println("检测到订单：" + order.getOrderNo() + "未支付，即将收单交易查询");
                    String result = alipayTemplate.querypay(order.getOrderNo());
                    if (StringUtils.isNotEmpty(result)) {
                        JSONObject jo = JSONObject.parseObject(result);
                        jo = jo.getJSONObject("alipay_trade_query_response");
                        if (jo.getString("trade_status").equals("TRADE_SUCCESS") || jo.getString("trade_status").equals("TRADE_FINISHED")) {
                            orderService.orderHandle(jo.getString("send_pay_date"), jo.getString("total_amount"), order);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
        });
    }
}
