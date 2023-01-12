package com.ifans.api.activiti.fallback;

import com.ifans.api.activiti.FeignActivitiService;
import com.ifans.api.activiti.FeignRefundActivitiService;
import com.ifans.api.activiti.domain.RefundVariables;
import com.ifans.api.system.fallback.FeignUserFallbackFactory;
import com.ifans.common.core.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignActivitiFallbackFactory implements FallbackFactory<FeignActivitiService> {
    private static final Logger log = LoggerFactory.getLogger(FeignActivitiFallbackFactory.class);

    @Override
    public FeignActivitiService create(Throwable throwable) {
        log.error("退款流程调用失败:{}", throwable.getMessage());
        return new FeignActivitiService() {

            @Override
            public R auditHis(String instanceId) {
                return R.failed("根据实例id查询审核流程历史，失败:" + throwable.getMessage());
            }

            @Override
            public R claimTask(String instanceId, String candidateGroupMatchStr) {
                return R.failed("拾取任务，失败:" + throwable.getMessage());
            }

            @Override
            public R completTask(String instanceId) {
                return R.failed("完成任务，失败:" + throwable.getMessage());
            }
        };
    }
}