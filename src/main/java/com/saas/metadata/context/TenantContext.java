package com.saas.metadata.context;

public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new InheritableThreadLocal<>();

    private TenantContext() {}

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void setCurrentTenant(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID must not be blank");
        }
        if (!tenantId.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid tenant ID format: " + tenantId);
        }
        CURRENT_TENANT.set(tenantId);
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
