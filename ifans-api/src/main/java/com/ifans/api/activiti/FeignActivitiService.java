package com.ifans.api.activiti;

import com.ifans.api.activiti.fallback.FeignActivitiFallbackFactory;
import com.ifans.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "ifans-activiti", contextId = "feignActivitiService", fallbackFactory = FeignActivitiFallbackFactory.class)
public interface FeignActivitiService {

    /**
     * 根据实例id查询审核流程历史
     */
    @GetMapping("/auditHis/{instanceId}")
    R auditHis(@PathVariable("instanceId") String instanceId);
}
