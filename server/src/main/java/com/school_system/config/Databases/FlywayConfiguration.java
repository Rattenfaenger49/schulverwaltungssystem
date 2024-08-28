package com.school_system.config.Databases;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;
@Slf4j
@Configuration
public class FlywayConfiguration {

    @Autowired
    private MultitenantDataSource multitenantDataSource;

    @Value("${spring.flyway.locations}")
    private String migrationLocation;
    @Value("${spring.flyway.baseline-on-migrate}")
    private boolean baselineOnMigrate = true;
    @Value("${spring.flyway.schemas}")
    private String schemas;
    @Value("${spring.flyway.create-schemas}")
    private boolean createSchemas = true;
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            Map<Object, Object> targetDataSources = multitenantDataSource.getTargetDataSources();
            for (Map.Entry<Object, Object> entry : targetDataSources.entrySet()) {
                DataSource dataSource = (DataSource) entry.getValue();
                // Perform Flyway migration for each DataSource when it's ready
                migrateFlywayForDataSource(dataSource);
            }
        };
    }

    private void migrateFlywayForDataSource(DataSource dataSource) {
        Flyway flywayInstance = Flyway.configure()
                .dataSource(dataSource)
                .locations(migrationLocation) // adjust the migration location as needed
                .baselineOnMigrate(baselineOnMigrate)
                .schemas(schemas)
                .createSchemas(createSchemas)
                .load();
        flywayInstance.migrate();
    }
}


/*

package com.school_system.config.Databases;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Configuration
public class FlywayConfiguration {

    @Autowired
    private MultitenantDataSource multitenantDataSource;

    @Value("${spring.flyway.locations}")
    private String migrationLocation;
    @Value("${spring.flyway.baseline-on-migrate}")
    private boolean baselineOnMigrate = true;
    @Value("${spring.flyway.schemas}")
    private String schemas;
    @Value("${spring.flyway.create-schemas}")
    private boolean createSchemas = true;

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            Map<Object, Object> targetDataSources = multitenantDataSource.getTargetDataSources();
            Map<DataSource, Flyway> flyways = new HashMap<>();
            List<DataSource> failedMigrations = new ArrayList<>();

            // Initialize Flyway instances for each DataSource
            for (Map.Entry<Object, Object> entry : targetDataSources.entrySet()) {
                DataSource dataSource = (DataSource) entry.getValue();
                Flyway flywayInstance = createFlywayInstance(dataSource);
                flyways.put(dataSource, flywayInstance);
            }

            // Perform migrations
            for (Map.Entry<DataSource, Flyway> entry : flyways.entrySet()) {
                DataSource dataSource = entry.getKey();
                Flyway flyway1 = entry.getValue();
                try {
                    flyway1.migrate();
                    log.info("Migration successful for dataSource: {}", dataSource);
                } catch (Exception e) {
                    log.error("Migration failed for dataSource: {}", dataSource, e);
                    failedMigrations.add(dataSource);
                }
            }

            // Handle rollbacks if any migrations failed
            if (!failedMigrations.isEmpty()) {
                handleFailedMigrations(failedMigrations, flyways);
            }
        };
    }

    private Flyway createFlywayInstance(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(migrationLocation)
                .baselineOnMigrate(baselineOnMigrate)
                .schemas(schemas)
                .createSchemas(createSchemas)
                .load();
    }

    private void handleFailedMigrations(List<DataSource> failedMigrations, Map<DataSource, Flyway> flyways) {
        // Rollback successfully applied migrations
        for (var dataSource : flyways.entrySet()) {
            if(failedMigrations.contains(dataSource.getKey()))
                continue;

            Flyway flyway = dataSource.getValue();
            try {
                flyway.undo(); // Undo the last migration
                log.info("Rollback successful for dataSource: {}", dataSource);
            } catch (Exception e) {
                log.error("Rollback failed for dataSource: {}", dataSource, e);
                // Handle rollback failure
            }
        }
    }
}
 */