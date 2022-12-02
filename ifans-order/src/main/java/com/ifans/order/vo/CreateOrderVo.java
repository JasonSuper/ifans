package com.ifans.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 下单参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderVo {

    private String token;

    private String goodsId;

    private int count;

    private int pay_money;

    private long timestamp;
}
