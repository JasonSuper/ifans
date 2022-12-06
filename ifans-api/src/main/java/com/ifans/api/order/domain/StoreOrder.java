package com.ifans.api.order.domain;

import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson2.annotation.JSONField;
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
 * 订单信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("store_order")
public class StoreOrder {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 下单时间
     */
    @TableField("order_time")
    private Date orderTime;

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;

    /**
     * 支付方式【1->支付宝；2->微信；3->银联； 4->货到付款；】
     */
    @TableField("pay_type")
    private int payType;

    /**
     * 支付时间
     */
    @TableField("payment_time")
    private Date paymentTime;

    /**
     * 商品总额
     */
    @TableField("order_price")
    @JSONField(name="orderPrice", serializeUsing = ToStringSerializer.class)
    private BigDecimal orderPrice;

    /**
     * 应付金额
     */
    @TableField("must_price")
    @JSONField(name="mustPrice", serializeUsing = ToStringSerializer.class)
    private BigDecimal mustPrice;

    /**
     * 实付金额
     */
    @TableField("pay_price")
    @JSONField(name="payPrice", serializeUsing = ToStringSerializer.class)
    private BigDecimal payPrice;

    /**
     * 订单状态 0待付款 1已付款 2已发货 3已完成 4已取消 5售后中 6售后完成
     */
    @TableField("pay_status")
    private int payStatus;

    /**
     * 订单状态 0正常 1停用
     */
    @TableField("status")
    private String status;

    @TableField("create_by")
    private String createBy;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_by")
    private String updateBy;

    @TableField("update_time")
    private Date updateTime;
}
