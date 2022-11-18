package com.ifans.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.ifans.gateway.handler.SentinelFallbackHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * 网关限流配置
 * 默认限流配置，并不是动态读取nacos方式
 *
 * 
 */
@Configuration
public class GatewayConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelFallbackHandler sentinelGatewayExceptionHandler() {
        return new SentinelFallbackHandler();
    }

    @Bean
    @Order(-1)
    public SentinelGatewayFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @PostConstruct
    public void init() {
        // 加载网关限流规则
        initGatewayRules();
    }

    /**
     * 网关限流规则
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(new GatewayFlowRule("system-api")
                .setCount(3) // 限流阈值
                .setIntervalSec(60)); // 统计时间窗口，单位是秒，默认是 1 秒

        rules.add(new GatewayFlowRule("code-api")
                .setCount(5) // 限流阈值
                .setIntervalSec(60));

        // 加载网关限流规则
        GatewayRuleManager.loadRules(rules);
        // 加载限流分组
        //initCustomizedApis();
    }

    /**
     * 限流分组
     */
    /*private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
        // ruoyi-system 组
        ApiDefinition api1 = new ApiDefinition("system-api").setPredicateItems(new HashSet<ApiPredicateItem>() {
            private static final long serialVersionUID = 1L;

            {
                // 匹配 /user 以及其子路径的所有请求
                add(new ApiPathPredicateItem().setPattern("/system/user/**")
                        .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
            }
        });
        // ruoyi-gen 组
        ApiDefinition api2 = new ApiDefinition("code-api").setPredicateItems(new HashSet<ApiPredicateItem>() {
            private static final long serialVersionUID = 1L;

            {
                // 只匹配 /job/list
                add(new ApiPathPredicateItem().setPattern("/code/gen/list"));
            }
        });
        definitions.add(api1);
        definitions.add(api2);
        // 加载限流分组
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }*/
}