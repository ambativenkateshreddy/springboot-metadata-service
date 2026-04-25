package com.saas.metadata.config;

import com.saas.metadata.context.TenantContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class TenantAwareDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }
}
