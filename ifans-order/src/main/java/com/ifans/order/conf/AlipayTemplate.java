package com.ifans.order.conf;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.ifans.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id;

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCPeZ330sj2ei+UF5OXm/bl97F6ln0+rCsEsTXhRLvbI3wg0o6Q6h1elglnyC5hfcqJcukhZSTZS9/SS+be59Ft69vQ6NtrFEdY6ORQWIOxyHkW5/eS3ePknVB88ADesVQjc2iJ08JI13rF/S0S5Ru7sozuiYLZcIzsfPtOqEPHIrvfLnP5JNpsoqzkxCTDcTeeatajMNm16yEQKDAa16l7l+QQ42yQF5pzOMaAIuzfG9s4l25VMkeoXwR3JsdTnjK1JIaS9ibqeyJLsdzZ9QxWkFGy0b/8aNmaeX/gONs02yhbdxdOmoOaNJ8N4pUVfczSda2VLT84OL6rMm/jQDKzAgMBAAECggEADLr2yEF8sVoAFLzOzLn6+0ayCbWGoWP3YTA0HqvRm00Xu3XTTopnVrgrV3kMnHvj17RJeTwXO+yejHNNpeOd0ooF9BBlP6gJ+JqZf4OLE+A81kp4ziBAE03eEWfeO+QyGf8ofGo8p+furdicAjwv7MSX+qZa5N42stkYF0+Yy/dn08dySgbP/atl8qTbd0jHAlpVIw7H0frUYs7iFFI5L/vwRZzFFr/0wi5g+7DjATzSDHqBIDKneLdyTA1I7jGzSAye3Jfxyc7i3jhoxik/6PGZVNAf7nsBK7/wPduIvGy6j5HLqxCUvCOKF3OqAT7JxlALLwxMSyuGkv6LhHovGQKBgQDT1s+wmJzhezjGzJ3LsuZtLq2+A6GfqzIg7+XKFkPsWCjs+YgaLCHtc57dzA6t0lNtZfSQnDpujAUzPbqY7uoMTSi4bKyQk5b3m1jMvf7xf1dKvZopMBi8jo7wIrBYcoZUFp4AA+DJJ/msc6yxOIEVWpFHbwSQH1DeSmdCfnadHwKBgQCtYm5uyTdGiUeTre2j2vRCcLx1Uh/wcbXb0nT8tAsyoSkhue+QZcM+7qVZGAT7u01TOBZb0hJSNGRAGVjxh/BxK4r5uVYeCFKX6GwgJyR1hZRot5t9rX+4Baj3S0GxpYTnc0cYwDb8UY1lBXbC7+04JXFuKEt98iDmH1MZ49qj7QKBgFfgzemxdasoReZabb9Z6LM0YQjpUrv89d+qBUZvNCAwdlHQNkC9PBnYE/hotVIwZUGFwQ3YcXj4hxO5sNXOjrMpsxHGq1OSllcDT7QnwUIHlHvB6djaRfi6nttJWKRoZgcuFr9k3HQ2LCH5HpOqIAputsPObzMmepKshOeVtLj7AoGAHhafzW/zvLTkC/e57p53lvQTcFShIEvLYGtkS9o+uzmGdsjajvdp64jkftKRQtIdOBzanHKXJVbcMtdzPO73RdxlkLKeBDYzhxMZeHbyd8aHm6iJPkUWjevFh6yyqM5tgvDZXK02nZDVdbt+AZQ7WWv+BDzP5PpYxTaYaDzHW30CgYEAgLqBI416UHncwtMdFw0wXfo0By2Ffwn+2PFM0RA6+RSMS3cOoX25Ne4YGFY+cE0PWMqzd5zNpDTs/sh+jiCCDPRTWmMHVRD7hDIA3BaJSjMRpVxVG+sFAa92TOJPI8PCoTcTL7WrOPD9M03mStAQj+RCQcS2z5U7qF8TxJVsO1w=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4NsBqe5nvdeU152nyBzMbXWHUci+QpssBnpcweq9rwAHzxg8zu06C7iaYm1BwEJ97q3nCpfk7QdBxZyMCQRFGX3Ec4eOGQ06ppItRs38eAsLgqreE+J5IX1e5PvtHgAzfKrL6UmAPAwcX+fl5Ei+rYMIKS76CMIV3hMIy0nslIp8Z3ywPDUbUqRnAcB6yfFPjKHsCWymalHuoZjIJs2/q9XGxW3yX3NkJRp5b2exzOCwc2e37n4AqR97vf/tbeXRsKqZgRcGbNB3+YLIP7JjaKGGPINetMHx9Kz+fnZXhBsaRWte4SmEPw3xwfZYWE1lB9DS7TnZuCPWHXbCwxG16QIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url;

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    // 自动关单时间
    private String timeout;

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        // 30分钟内不付款就会自动关单
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest);
        String result = response.getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        return result;
    }

    /**
     * 交易关闭
     */
    public boolean closepay(String orderSn) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl, app_id, merchant_private_key, "json", charset, alipay_public_key, sign_type);
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        request.setBizContent("{" +
                "    \"out_trade_no\":\"" + orderSn + "\"" +
                "  }");
        AlipayTradeCloseResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        return response.isSuccess();
    }
}
