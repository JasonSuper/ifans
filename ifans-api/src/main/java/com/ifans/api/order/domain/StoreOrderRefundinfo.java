package com.ifans.api.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退款信息表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("store_order_refundinfo")
public class StoreOrderRefundinfo {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private String orderId;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 退款单编号
     */
    @TableField("refund_no")
    private String refundNo;

    /**
     * 退款状态 0成功; 1失败
     */
    @TableField("refund_status")
    private String refundStatus;

    /**
     * 退款响应内容
     */
    @TableField("refund_subject")
    private String refundSubject;

    /**
     * 原订单金额
     */
    @TableField("total_money")
    private BigDecimal totalMoney;

    /**
     * 退款金额
     */
    @TableField("refund_money")
    private BigDecimal refundMoney;

    /**
     * 退款原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 申请退款时间
     */
    @TableField("refund_time")
    private Date refundTime;

    /**
     * 退款执行成功时间
     */
    @TableField("gmt_refund_pay")
    private Date gmtRefundPay;
}
