package com.ifans.api.order.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("act_bus_realtion_refundapply")
public class ActBusRealtionRefundapply {
    private String id;

    /**
     * 退款流程实例ID
     */
    @TableField("activiti_instance_id")
    private String activitiInstanceId;

    /**
     * 流程与业务绑定ID
     */
    @TableField("act_bus_relation_id")
    private String actBusRelationId;

    /**
     * 业务流程ID
     */
    @TableField("bus_id")
    private String busId;

    public ActBusRealtionRefundapply(String activitiInstanceId, String actBusRelationId, String busId) {
        this.activitiInstanceId = activitiInstanceId;
        this.actBusRelationId = actBusRelationId;
        this.busId = busId;
    }
}
