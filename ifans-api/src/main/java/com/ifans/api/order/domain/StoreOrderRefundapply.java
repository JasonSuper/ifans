package com.ifans.api.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ifans.common.core.annotation.Excel;
import com.ifans.common.core.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 退款申请对象 store_order_refundapply
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreOrderRefundapply extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 退款申请ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 退款申请ID
     */
    @TableField("order_id")
    @Excel(name = "退款申请ID")
    private String orderId;

    /**
     * 退款流程实例ID
     */
    @TableField("activiti_instance_id")
    private String activitiInstanceId;

    /**
     * 申请时间
     */
    @TableField("apply_time")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请时间" , width = 30, dateFormat = "yyyy-MM-dd")
    private Date applyTime;

    /**
     * 退款原因
     */
    @TableField("refund_reason")
    @Excel(name = "退款原因")
    private String refundReason;

    /**
     * 审核状态 0未审核 1审核中 2通过 3驳回
     */
    @TableField("bpm_status")
    @Excel(name = "审核状态 0未审核 1审核中 2通过 3驳回")
    private Long bpmStatus;
}
