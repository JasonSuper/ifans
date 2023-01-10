package com.ifans.api.activiti;

import com.ifans.api.activiti.domain.RefundVariables;
import com.ifans.api.activiti.fallback.FeignRefundActivitiFallbackFactory;
import com.ifans.common.core.constant.SecurityConstants;
import com.ifans.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "ifans-activiti", path = "/refund", contextId = "feignRefundActivitiService", fallbackFactory = FeignRefundActivitiFallbackFactory.class)
public interface FeignRefundActivitiService {

    /**
     * 根据实例id查询审核流程历史
     */
    @GetMapping("/processInstanceHistoricTask/{instanceId}")
    R processInstanceHistoricTask(@PathVariable("instanceId") String instanceId);

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

    /**
     * 拾取任务
     *
     * @param instanceId 流程实例id
     * @param candidateGroupMatchStr 所属责任组，用于流程权限判断
     */
    @PostMapping(value = "/claimTask")
    R claimTask(@RequestParam("instanceId") String instanceId, @RequestParam("candidateGroupMatchStr") String candidateGroupMatchStr);

    /**
     * 完成任务
     */
    @PostMapping(value = "/completTask")
    R completTask(String instanceId);
}
