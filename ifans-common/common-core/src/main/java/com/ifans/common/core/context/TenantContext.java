package com.ifans.common.core.context;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多租户 tenant_id存储器
 */
public class TenantContext {
    private static final Logger log = LoggerFactory.getLogger(TenantContext.class);

    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setTenant(String tenant) {
        log.debug(" setting tenant to " + tenant);
        currentTenant.set(tenant);
    }

    public static String getTenant() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}
