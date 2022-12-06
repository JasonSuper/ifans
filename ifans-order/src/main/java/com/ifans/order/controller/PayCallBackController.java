package com.ifans.order.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.ifans.order.conf.AlipayTemplate;
import com.ifans.order.service.OrderService;
import com.ifans.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/payCallBack")
public class PayCallBackController {

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;

    /**
     * 支付异步回调接口
     */
    @RequestMapping("/alipay")
    public String payCallBack(PayAsyncVo vo, HttpServletRequest request) throws AlipayApiException {
        System.out.println("收到支付宝最后的通知数据：" + JSON.toJSONString(vo));
        // 验签
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        // 只要我们收到了支付宝给我们的异步通知 验签成功 我们就要给支付宝返回success
        if (AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type())) {
            return orderService.handlePayResult(vo);
        }
        System.err.println("受到恶意验签攻击");
        return "fail";
    }
}
