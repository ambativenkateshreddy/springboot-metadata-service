package com.saas.metadata.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TenantRegistry {

    private final Set<String> registeredTenants = ConcurrentHashMap.newKeySet();
    private final Map<String, TenantInfo> tenantMetadata = new ConcurrentHashMap<>();

    public void register(String tenantId, TenantInfo info) {
        registeredTenants.add(tenantId);
        tenantMetadata.put(tenantId, info);
        log.info("Tenant registered: {}", tenantId);
    }

    public boolean isRegistered(String tenantId) {
        return tenantId != null && registeredTenants.contains(tenantId.toLowerCase());
    }

    public Set<String> getAllTenants() {
        return Collections.unmodifiableSet(registeredTenants);
    }

    public TenantInfo getInfo(String tenantId) {
        return tenantMetadata.get(tenantId);
    }

    public record TenantInfo(String tenantId, String schemaName, String displayName, boolean active) {}
}
