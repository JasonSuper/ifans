package com.ifans.order.controller;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifans.api.order.domain.StoreOrder;
import com.ifans.api.order.vo.StoreOrderVo;
import com.ifans.api.store.FeignStoreService;
import com.ifans.api.store.domain.StoreGoods;
import com.ifans.common.core.util.R;
import com.ifans.common.core.util.ReflectMapUtils;
import com.ifans.common.security.util.SecurityUtils;
import com.ifans.order.enums.OrderStatusEnum;
import com.ifans.order.pay.AliPayTemplate;
import com.ifans.order.service.OrderService;
import com.ifans.order.vo.CreateOrderVo;
import com.ifans.order.vo.PayVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class OrderController {

    @Autowired
    private AliPayTemplate alipayTemplate;
    //@Autowired
    //private YzfPayTemplate yzfPayTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FeignStoreService feignStoreService;
    @Autowired
    private RedissonClient redissonClient;

    // 生成下单页面ticket的key前缀，此key用于幂等性创建订单
    public static final String ORDER_PREPARE_KEY = "order-prepare:";
    // 订单缓存key前缀
    public static final String ORDER = "order:";
    // 加密解密密钥
    private final byte[] key = "123!@#45".getBytes();
    //构建加密工具
    private SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.DES, key);

    @PostMapping("/list")
    public R list(@RequestBody Page page) {
        IPage<StoreOrderVo> list = orderService.pageList(page, 0);
        return R.ok(list);
    }

    @PostMapping("/createOrder")
    public R createOrder(@RequestBody CreateOrderVo createOrderVo) {
        String lockkey = SecurityUtils.getUser().getId() + createOrderVo.getGoodsId() + createOrderVo.getCount();
        RLock lock = redissonClient.getLock(lockkey);
        lock.lock(20, TimeUnit.SECONDS);

        try {
            // 返回对象
            Map<String, Object> result = new HashMap<>();

            // 查询商品
            R storeResult = feignStoreService.info(createOrderVo.getGoodsId());
            if (storeResult.getCode() != 200 || storeResult.getData() == null || StringUtils.isEmpty(storeResult.getData().toString())) {
                result.put("code", "500");
                result.put("msg", "商品不存在");
                return R.ok(result);
            }

            // 校验价格
            StoreGoods storeGoodsInfo = JSON.parseObject(storeResult.getData().toString(), StoreGoods.class);
            BigDecimal pay_money = storeGoodsInfo.getPrice().multiply(new BigDecimal(createOrderVo.getCount()));
            if (pay_money.compareTo(createOrderVo.getPay_money()) != 0) {
                result.put("code", "500");
                result.put("msg", "价格发生变化");
                return R.ok(result);
            }

            // 是否再下一单 0否 1是
            if (createOrderVo.getAgain() != 1) {
                // 获取最新的，具有相同购买属性（道具id，购买数量）的订单
                StoreOrder storeOrder = orderService.searchLastExistOrder(createOrderVo);
                if (storeOrder != null) {
                    result.put("order_id", storeOrder.getId());
                    result.put("code", "500");
                    result.put("msg", "有尚未完成订单，点击查看");
                    return R.ok(result);
                }
            }

            // 初始化订单，发送MQ和进行redis缓存
            String orderId = orderService.initOrder(createOrderVo);
            result.put("code", "200");
            result.put("msg", "成功");
            result.put("orderId", orderId);
            return R.ok(result);
        } finally {
            lock.unlock();
        }
    }

    @PostMapping(value = "/pay")
    public R pay(@RequestBody Map<String, String> params) throws Exception {
        if (params.get("orderId") == null || StringUtils.isEmpty(params.get("orderId"))) {
            throw new Exception("参数错误");
        }

        StoreOrder order = orderService.getById(params.get("orderId"));
        RLock lock = redissonClient.getLock(order.getOrderNo());

        // 加锁避免并发操作，例如：在支付的时候，其他微服务并发进行取消、修改订单等操作
        lock.lock(20, TimeUnit.SECONDS);
        try {
            Map<String, Object> result = new HashMap<>();

            order.setPayType(5); // 默认易支付支付
            orderService.updateById(order);

            if (order != null && OrderStatusEnum.CREATE_NEW.getCode() == order.getPayStatus()) {
                // 根据订单号到数据库查询订单消息，这里偷懒，直接赋值
                PayVo payVo = new PayVo();
                payVo.setOut_trade_no(order.getOrderNo()); // 商户订单号
                payVo.setSubject("道具购买"); // 订单名称
                payVo.setTotal_amount(order.getMustPrice().toString()); // 付款金额
                payVo.setBody(""); //商品描述 可空

                String payPage = alipayTemplate.pay(payVo);
                //String payPage = yzfPayTemplate.payApi(payVo);

                result.put("payPage", payPage);
                result.put("code", "200");
                result.put("msg", "成功");
                return R.ok(result);
            } else {
                result.put("code", "500");
                result.put("msg", "订单已支付，请勿重复操作");
                return R.ok(result);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 取消支付
     *
     * @param orderNo 订单号
     * @throws AlipayApiException
     */
    @GetMapping("/closepay/{orderNo}")
    public R closepay(@PathVariable String orderNo) throws AlipayApiException {
        RLock lock = redissonClient.getLock(orderNo);

        // 加锁避免并发操作，例如：在取消支付的时候，其他微服务并发进行支付、修改订单等操作
        lock.lock(20, TimeUnit.SECONDS);
        boolean isClose = false;
        try {
            StoreOrder order = orderService.findByOrderNo(orderNo);
            if (order.getPayStatus() == OrderStatusEnum.CREATE_NEW.getCode()) {
                isClose = switch (order.getPayType()) {
                    case 1:
                        //yield alipayTemplate.closepay(orderNo);
                    default:
                        yield true;
                };

                if (isClose) {
                    order.setPayStatus(OrderStatusEnum.CANCLED.getCode());
                    order.setUserId(SecurityUtils.getUser().getId());
                    order.setUpdateTime(new Date());
                    orderService.updateById(order);
                    // 清除redis缓存
                    orderService.cleanOrderRedis(order.getId(), order.getUserId());
                }
            }
            return isClose ? R.ok("取消成功！") : R.failed("取消失败！");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 生成商品购买参数ticket
     *
     * @return 商品和购买量加密后的ticket
     */
    @PostMapping("/ticket/prepare")
    public R prepare(@RequestBody CreateOrderVo createOrderVo) {
        /*ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.putLong(Long.valueOf(createOrderVo.getGoodsId()));
        buffer.putInt(createOrderVo.getCount());
        buffer.putLong(new Date().getTime());

        //加密
        String ticket = aes.encryptHex(buffer.array());*/

        long ticket = IdWorker.getId();
        // 可额外进行一些优惠券，联系方式等缓存
        // do something...
        redisTemplate.opsForValue().set(ORDER_PREPARE_KEY + ticket, createOrderVo, 30, TimeUnit.MINUTES);
        return R.ok(ticket + "");
    }

    /**
     * 确认订单信息
     *
     * @param ticket 商品购买参数ticket
     * @return
     */
    @GetMapping("/confrimInfo/{ticket}")
    public R confrimInfo(@PathVariable String ticket) {
        /*byte[] decryptByte = aes.decrypt(ticket);
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.put(decryptByte);
        buffer.flip();

        String goodsId = Long.toString(buffer.getLong());
        int count = buffer.getInt();
        long time = buffer.getLong();

        long diff = (new Date().getTime() - time) / 1000 / 60;*/

        Object obj = redisTemplate.opsForValue().get(ORDER_PREPARE_KEY + ticket);
        if (obj == null) { // 不是30分钟内的请求
            return R.failed("当前页面已失效，请返回详情页重新下单");
        }

        CreateOrderVo createOrderVo = (CreateOrderVo) obj;
        R r = feignStoreService.info(createOrderVo.getGoodsId());
        if (r.getData() != null) {
            StoreGoods storeGoods = JSON.parseObject(r.getData().toString(), StoreGoods.class);
            Map<String, Object> result = ReflectMapUtils.objectToMap(storeGoods);
            BigDecimal pay_price = new BigDecimal(result.get("price").toString()).multiply(new BigDecimal(createOrderVo.getCount()));
            result.put("pay_price", pay_price.toString());
            result.put("count", createOrderVo.getCount());
            return R.ok(result);
        } else {
            return R.failed("出现异常！");
        }
    }

    public static void main(String[] args) {
        /*int a = 9;
        byte[] b = ByteUtils.int2Hbytes(a);
        System.out.println(b.length);
        String c = HexUtil.encodeHexStr(b);
        System.out.println(c.length());
        System.out.println(Integer.toBinaryString(a));*/
    }
}
