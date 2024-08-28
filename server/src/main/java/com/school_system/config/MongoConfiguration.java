package com.school_system.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfiguration extends AbstractMongoClientConfiguration {

    @Value("${mongodb.authentication-database}")
    private String authDatabase;

    @Value("${mongodb.database}")
    private String database;

    @Value("${mongodb.username}")
    private String username;

    @Value("${mongodb.password}")
    private String password;

    @Value("${mongodb.port}")
    private int port;

    @Value("${mongodb.host}")
    private String host;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Bean
    public MongoClient mongoClient() {
        MongoCredential credential = MongoCredential.createCredential(username, authDatabase, password.toCharArray());

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(String.format("mongodb://%s:%d", host, port)))
                .credential(credential)
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(1, TimeUnit.SECONDS)  // Set the connection timeout to 1 second
                                .readTimeout(1, TimeUnit.SECONDS)     // Set the read timeout to 1 second
                )
                .applyToServerSettings(builder ->
                        builder.heartbeatFrequency(25, TimeUnit.MILLISECONDS)
                                .heartbeatFrequency(1, TimeUnit.SECONDS)// Heartbeat frequency
                                  // Heartbeat connect timeout
                )
                .applyToClusterSettings(builder ->
                        builder.serverSelectionTimeout(1, TimeUnit.SECONDS) // Set the server selection timeout to 1 second
                )
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}