package com.saas.metadata.tenant;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, Timer> routingTimers = new ConcurrentHashMap<>();

    public TenantRoutingDataSource(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            log.warn("No tenant in context — falling back to default datasource");
            return "default";
        }

        Timer timer = routingTimers.computeIfAbsent(tenantId, id ->
            Timer.builder("tenant.schema.resolution")
                .tag("tenant", id)
                .description("Time to resolve tenant datasource")
                .register(meterRegistry)
        );

        return timer.record(() -> {
            log.debug("Routing to tenant schema: {}", tenantId);
            return tenantId;
        });
    }
}
