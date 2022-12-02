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
     * 购买数
     */
    @TableField("num")
    private Integer num;

    /**
     * 商品总额
     */
    @TableField("order_price")
    private BigDecimal orderPrice;

    /**
     * 应付金额
     */
    @TableField("must_price")
    private BigDecimal mustPrice;

    /**
     * 实付金额
     */
    @TableField("pay_price")
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
