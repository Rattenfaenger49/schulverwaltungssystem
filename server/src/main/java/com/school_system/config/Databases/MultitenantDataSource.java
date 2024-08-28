package com.school_system.config.Databases;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


import java.util.HashMap;

import java.util.Map;

@Getter
@Slf4j
public class MultitenantDataSource extends AbstractRoutingDataSource {

    private Map<Object, Object> targetDataSources = new HashMap<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }

    @Override
    public void afterPropertiesSet() {
        // Ensure targetDataSources is properly initialized
        if (this.targetDataSources == null || this.targetDataSources.isEmpty()) {
            throw new IllegalArgumentException(String.format("Die Eigenschaft '%s' ist Pflichtfeld.", targetDataSources));
        }

        super.afterPropertiesSet();
    }



    public void setTargetDataSources(Map<Object, Object> targetDataSources) {

        targetDataSources.forEach( (k, v) -> {

        });
        this.targetDataSources = targetDataSources;
        super.setTargetDataSources(targetDataSources);
    }
}
