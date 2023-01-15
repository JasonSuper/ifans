package com.ifans.api.order;

import com.ifans.api.system.fallback.FeignUserFallbackFactory;
import com.ifans.common.core.constant.SecurityConstants;
import com.ifans.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "ifans-order", contextId = "actbusrelation", fallbackFactory = FeignUserFallbackFactory.class)
public interface FeignActBusRelationService {

    /**
     * 修改相关业务表的审核状态
     * @param processInstanceId 流程实例ID
     * @param bpmStatus 审核状态
     * @param businessKey 相关业务的ID
     * @return
     */
    @GetMapping(value = "/updateBusBpmStatus", headers = SecurityConstants.HEADER_FROM_IN)
    R updateBusBpmStatus(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("int bpmStatus") int bpmStatus, @RequestParam("businessKey") String businessKey);
}
