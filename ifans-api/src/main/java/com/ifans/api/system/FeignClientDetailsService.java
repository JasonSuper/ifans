package com.ifans.api.system;

import com.ifans.api.system.domain.SysOauthClientDetails;
import com.ifans.api.system.fallback.FeignClientFallbackFactory;
import com.ifans.common.core.constant.SecurityConstants;
import com.ifans.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "ifans-system", path = "client", contextId="feignClientDetailsService", fallbackFactory = FeignClientFallbackFactory.class)
public interface FeignClientDetailsService {

    /**
     * 通过clientId 查询客户端信息
     *
     * @param clientId 用户名
     * @return R
     */
    @GetMapping(value = "/getClientDetailsById/{clientId}", headers = SecurityConstants.HEADER_FROM_IN)
    R<SysOauthClientDetails> getClientDetailsById(@PathVariable("clientId") String clientId);

    /**
     * 查询全部客户端
     *
     * @return R
     */
    @GetMapping(value = "/list", headers = SecurityConstants.HEADER_FROM_IN)
    R<List<SysOauthClientDetails>> listClientDetails();
}
