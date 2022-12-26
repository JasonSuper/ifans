package com.ifans.api.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单详细项
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreOrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField("order_id")
    private String orderId;

    @TableField("order_no")
    private String orderNo;

    @TableField("goods_id")
    private String goodsId;

    @TableField("goods_name")
    private String goodsName;

    @TableField("goods_icon")
    private String goodsIcon;

    @TableField("num")
    private int num;
}
