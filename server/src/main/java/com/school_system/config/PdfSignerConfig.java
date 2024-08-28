package com.school_system.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PdfSignerConfig {

    // Getters
    @Value("${signing.keystore.path}")
    private String keystorePath;

    @Value("${signing.keystore.password}")
    private String keystorePassword;

}