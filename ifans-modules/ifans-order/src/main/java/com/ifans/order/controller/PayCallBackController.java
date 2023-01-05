package com.ifans.order.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.ifans.order.pay.AliPayTemplate;
import com.ifans.order.service.OrderService;
import com.ifans.order.vo.AliPayAsyncVo;
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
    private AliPayTemplate alipayTemplate;
    //@Autowired
    //private YzfPayTemplate yzfPayTemplate;
    @Autowired
    private OrderService orderService;

    /**
     * 支付异步回调接口
     */
    @RequestMapping("/alipay")
    public String payCallBack(AliPayAsyncVo vo, HttpServletRequest request) throws AlipayApiException {
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
            try {
                return orderService.handlePayResult(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("受到恶意验签攻击");
        }
        return "fail";
    }

    /**
     * 易支付异步回调接口
     */
    /*@RequestMapping("/yzfpay")
    public String payCallBack(YzfPayAsyncVo vo) {
        System.out.println("收到易支付最后的通知数据：" + JSON.toJSONString(vo));

        Map<String, Object> param = ReflectMapUtils.beanToMap(vo);
        param.remove("sign");
        param.remove("sign_type");

        //参数排序，不能urlEncode和转小写
        String s = yzfPayTemplate.formatUrlMap(param, false, false);
        //加密生成签名
        String sign = DigestUtils.md5Hex(s + yzfPayTemplate.getKey());

        // 只要我们收到了易支付给我们的异步通知 验签成功 我们就要给易支付返回success
        //if (sign.equals(vo.getSign())) {
        if (yzfPayTemplate.getPid().equals(vo.getPid())) {
            return orderService.handlePayResult(vo);
        } else {
            System.err.println("受到恶意验签攻击");
        }
        return "fail";
    }*/
}
