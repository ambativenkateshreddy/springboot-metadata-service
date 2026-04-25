package com.saas.metadata.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataSourceConfig {

    // Step 1: Read as String
    @Value("${multitenancy.tenants:tenant_acme,tenant_globex,tenant_initech}")
    private String tenants;

    // Step 2: Convert to List
    private List<String> tenantList;

    @PostConstruct
    public void init() {
        tenantList = Arrays.stream(tenants.split(","))
                           .map(String::trim)
                           .toList();
    }

    public void printTenants() {
        // ✅ Correct loop
        for (String tenant : tenantList) {
            System.out.println(tenant);
        }

        // ✅ Correct access
        System.out.println(tenantList.get(0));
    }
}