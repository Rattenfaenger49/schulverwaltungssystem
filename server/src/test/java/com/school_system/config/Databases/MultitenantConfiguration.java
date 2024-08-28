package com.school_system.config.Databases;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Configuration
public class MultitenantConfiguration {

    @Value("${defaultTenant}")
    private String defaultTenant;
    @Autowired
    private Environment env;
    @Bean
    @ConfigurationProperties(prefix = "multi.datasource")
    @Primary
    public DataSource dataSource() {

        Map<Object, Object> resolvedDataSources = new HashMap<>();
        String[] dataSourceKeys = Objects.requireNonNull(env.getProperty("multi.datasource.keys")).split(",");

        for (String dataSourceKey : dataSourceKeys) {
            DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
            String prefix = "multi.datasource." + dataSourceKey + ".";
            String url = env.getProperty(prefix + "url");
            String username = env.getProperty(prefix + "username");
            String password = env.getProperty(prefix + "password");
            String driverClassName = env.getProperty(prefix + "driver-class-name");

            TenantRegistry.addTenant(dataSourceKey);

            dataSourceBuilder.driverClassName(driverClassName);
            dataSourceBuilder.username(username);
            dataSourceBuilder.password(password);
            dataSourceBuilder.url(url);
            resolvedDataSources.put(dataSourceKey, dataSourceBuilder.build());
        }

        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        dataSource.setTargetDataSources(resolvedDataSources);

        dataSource.afterPropertiesSet();
        return dataSource;
    }


}
