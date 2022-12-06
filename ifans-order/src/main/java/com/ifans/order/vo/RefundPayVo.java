package com.ifans.order.vo;

import lombok.Data;

@Data
public class RefundPayVo {

    /**
     * 订单号
     */
    private String out_trade_no;

    /**
     * 退款金额。
     * 需要退款的金额，该金额不能大于订单金额，单位为元，支持两位小数
     */
    private String refund_amount;

    /**
     * 退款请求号。
     * 标识一次退款请求，需要保证在交易号下唯一，如需部分退款，则此参数必传。
     * 注：针对同一次退款请求，如果调用接口失败或异常了，重试时需要保证退款请求号不能变更，防止该笔交易重复退款。支付宝会保证同样的退款请求号多次请求只会退一次
     */
    private String out_request_no;
}
