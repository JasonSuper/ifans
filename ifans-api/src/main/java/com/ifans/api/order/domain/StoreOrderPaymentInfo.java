package com.ifans.api.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("store_order_paymentinfo")
public class StoreOrderPaymentInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 订单号（对外业务号）
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 订单id
     */
    @TableField("order_id")
    private String orderId;

    /**
     * 支付宝交易流水号
     */
    @TableField("alipay_trade_no")
    private String alipayTradeNo;

    /**
     * 支付总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 交易内容
     */
    @TableField("pay_subject")
    private String paySubject;

    /**
     * 支付状态
     */
    @TableField("payment_status")
    private String paymentStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 确认时间
     */
    @TableField("confirm_time")
    private Date confirmTime;

    /**
     * 回调内容
     */
    @TableField("callback_content")
    private String callbackContent;

    /**
     * 回调时间
     */
    @TableField("callback_time")
    private Date callbackTime;

}
