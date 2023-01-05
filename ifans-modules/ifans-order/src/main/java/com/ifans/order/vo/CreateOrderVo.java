package com.ifans.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 下单参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long timestamp;

    private String goodsId;

    private int count;

    private BigDecimal pay_money;

    // 再下一单 0否 1是
    private int again;
}
