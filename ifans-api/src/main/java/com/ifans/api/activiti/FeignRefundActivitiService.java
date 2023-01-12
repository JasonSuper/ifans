package com.ifans.api.activiti;

import com.ifans.api.activiti.domain.RefundVariables;
import com.ifans.api.activiti.fallback.FeignRefundActivitiFallbackFactory;
import com.ifans.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "ifans-activiti", path = "refund", contextId = "feignRefundActivitiService", fallbackFactory = FeignRefundActivitiFallbackFactory.class)
public interface FeignRefundActivitiService {

    /**
     * 启动一个流程实例 只进行流程启动不进入下一步
     */
    @PostMapping(value = "/startProcessAndSetGlobalVariable")
    R startProcessAndSetGlobalVariable(@RequestBody RefundVariables refundVariables);

    /**
     * 启动一个流程实例 进行流程启动且进入下一步
     */
    @PostMapping(value = "/startProcessAndSetGlobalVariableTo")
    R startProcessAndSetGlobalVariableTo(@RequestBody RefundVariables refundVariables, @RequestParam("candidateGroupMatchStr") String candidateGroupMatchStr);
}
