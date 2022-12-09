package com.ifans.order.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class YzfPayAsyncVo {

	private String pid;

	/**
	 * 易支付订单号
	 */
	private String 	trade_no;

	/**
	 * 商户系统内部的订单号
	 */
	private String 	out_trade_no;

	/**
	 * 支付方式列表
	 */
	private String 	type;

	/**
	 * VIP会员
	 */
	private String 	name;

	/**
	 * 退款金额
	 */
	private String 	money;

	/**
	 * 只有TRADE_SUCCESS是成功
	 */
	private String 	trade_status;

	private String 	param;

	private String 	sign;

	private String 	sign_type;
}
