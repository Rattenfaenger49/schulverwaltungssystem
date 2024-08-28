package com.school_system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "myapp")
public class MyAppProperties {

    // Getters and setters
    private String templatesPath;
    private String domainName;
    private String clientInfoPath;


}
