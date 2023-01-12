package com.ifans.api.activiti.fallback;

import com.ifans.api.activiti.FeignRefundActivitiService;
import com.ifans.api.activiti.domain.RefundVariables;
import com.ifans.api.system.fallback.FeignUserFallbackFactory;
import com.ifans.common.core.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignRefundActivitiFallbackFactory implements FallbackFactory<FeignRefundActivitiService> {
    private static final Logger log = LoggerFactory.getLogger(FeignUserFallbackFactory.class);

    @Override
    public FeignRefundActivitiService create(Throwable throwable) {
        log.error("退款流程调用失败:{}", throwable.getMessage());
        return new FeignRefundActivitiService() {

            @Override
            public R startProcessAndSetGlobalVariable(RefundVariables refundVariables) {
                return R.failed("启动一个流程实例（只进行流程启动不进入下一步），失败:" + throwable.getMessage());
            }

            @Override
            public R startProcessAndSetGlobalVariableTo(RefundVariables refundVariables, String candidateGroupMatchStr) {
                return R.failed("启动一个流程实例（进行流程启动且进入下一步），失败:" + throwable.getMessage());
            }
        };
    }
}