package com.ifans.api.activiti.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 退款流程参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundVariables {

    /**
     * 退款申请id
     */
    @NotNull
    private String refundApplyId;

    /**
     * 退款费用
     */
    @NotNull(message = "退款费用不能为空")
    private BigDecimal price;

    /**
     * 申请人Id
     */
    @NotNull(message = "申请人Id不能为空")
    private String userId;

    /**
     * 申请人名字
     */
    @NotNull(message = "申请人名字不能为空")
    private String userName;

//    /**
//     * 审批状态
//     */
//    private int status;
}
