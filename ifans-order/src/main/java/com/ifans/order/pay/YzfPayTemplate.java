package com.ifans.order.pay;

import cn.hutool.http.HttpUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ifans.order.vo.PayVo;
import com.ifans.order.vo.RefundPayVo;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.*;

@ConfigurationProperties(prefix = "yzf")
@Component
@Data
public class YzfPayTemplate {

    private String pid = "1315";
    private String key;
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 易支付会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url;
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url;

    /**
     * 页面跳转支付
     * 此接口可用于用户前台直接发起支付，使用form表单跳转或拼接成url跳转
     */
    public String pay(PayVo vo) {
        //参数存入 map
        Map<String, Object> param = new HashMap<>();
        param.put("pid", pid);
        param.put("type", "alipay");
        param.put("out_trade_no", vo.getOut_trade_no());
        param.put("notify_url", notify_url);
        param.put("return_url", return_url);
        param.put("name", vo.getSubject());
        param.put("money", vo.getTotal_amount());

        //参数排序，不能urlEncode和转小写
        String s = formatUrlMap(param, false, false);
        //加密生成签名
        String sign = DigestUtils.md5Hex(s + key);

        //跳转支付的url地址
        String url = "https://juea.cn/submit.php?" + s + "&sign=" + sign + "&sign_type=MD5";

        // 发送请求
        String result = HttpUtil.post(url, param);
        return result;
    }

    /**
     * API接口支付
     * 此接口可用于服务器后端发起支付请求，会返回支付二维码链接或支付跳转url
     */
    public String payApi(PayVo vo) {
        //参数存入 map
        Map<String, Object> param = new HashMap<>();
        param.put("pid", pid);
        param.put("type", "alipay");
        param.put("out_trade_no", vo.getOut_trade_no());
        param.put("notify_url", notify_url);
        param.put("return_url", return_url);
        param.put("name", vo.getSubject());
        param.put("money", vo.getTotal_amount());
        param.put("clientip", vo.getClientip());
        param.put("device", "pc");

        //参数排序，不能urlEncode和转小写
        String s = formatUrlMap(param, false, false);
        //加密生成签名
        String sign = DigestUtils.md5Hex(s + key);

        //跳转支付的url地址
        String url = "https://juea.cn/mapi.php?" + s + "&sign=" + sign + "&sign_type=MD5";

        // 发送请求
        String result = HttpUtil.post(url, param);

        if (StringUtils.isNotEmpty(result)) {
            JSONObject jo = JSON.parseObject(result);
            if (jo.getInteger("code") == 1) {
                return jo.getString("payurl");
            }
            return null;
        }
        return null;
    }

    /**
     * 交易退款
     */
    public String refundpay(RefundPayVo vo) {
        //参数存入 map
        Map<String, Object> param = new HashMap<>();
        param.put("pid", pid);
        param.put("key", key);
        param.put("out_trade_no", vo.getOut_trade_no());
        param.put("money", vo.getRefund_amount());
        //跳转支付的url地址
        String url = "https://juea.cn/api.php?act=refund";
        // 发送请求
        String result = HttpUtil.post(url, param);

        JSONObject jo = JSON.parseObject(result);
        if (jo.getInteger("code") == 1) {
            System.out.println("交易退款，调用成功，返回结果：" + jo.getString("msg"));
            return result;
        } else {
            System.out.println("交易退款，调用失败，返回码：" + jo.getInteger("code") + "，返回描述：" + jo.getString("msg"));
            return null;
        }
    }

    /**
     * 查询单个订单
     */
    public String querypay(String orderNo) {
        String url = "https://juea.cn/api.php?act=order&pid=" + pid + "&key=" + key + "&out_trade_no=" + orderNo;
        String result = HttpUtil.get(url);

        JSONObject jo = JSON.parseObject(result);
        if (jo.getInteger("code") == 1) {
            System.out.println("交易查询，调用成功，返回结果：" + result);
            return result;
        } else {
            System.out.println("交易查询，调用失败，返回码：" + jo.getInteger("code") + "，返回描述：" + result);
            return null;
        }
    }

    /**
     * 方法用途: 对所有传入参数按照字段名的Unicode码从小到大排序（字典序），并且生成url参数串<br>
     * 实现步骤: <br>
     *
     * @param paraMap    要排序的Map对象
     * @param urlEncode  是否需要URLENCODE
     * @param keyToLower 是否需要将Key转换为全小写。true:key转化成小写，false:不转化
     * @return
     */
    public String formatUrlMap(Map<String, Object> paraMap, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        Map<String, Object> tmpMap = paraMap;
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
                @Override
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
                if (StringUtils.isNotBlank(item.getKey())) {
                    String key = item.getKey();
                    String val = item.getValue() == null ? null : item.getValue().toString();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower) {
                        buf.append(key.toLowerCase() + "=" + val);
                    } else {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }
            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return buff;
    }
}
