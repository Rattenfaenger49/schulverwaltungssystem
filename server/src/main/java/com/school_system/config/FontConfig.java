package com.school_system.config;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FontConfig {

    @Value("${paths.font}")
    private String FONT_DIR;

    public String POPPINS_REGULAR;
    public String POPPINS_MEDIUM;
    public String POPPINS_THIN;
    public String POPPINS_BOLD;

    @PostConstruct
    public void init() {
        POPPINS_REGULAR = FONT_DIR + "Poppins-Regular.ttf";
        POPPINS_MEDIUM = FONT_DIR + "Poppins-Medium.ttf";
        POPPINS_THIN = FONT_DIR + "Poppins-Thin.ttf";
        POPPINS_BOLD = FONT_DIR + "Poppins-Bold.ttf";
    }

    public String getPoppinsRegular() {
        return POPPINS_REGULAR;
    }

    public String getPoppinsMedium() {
        return POPPINS_MEDIUM;
    }

    public String getPoppinsThin() {
        return POPPINS_THIN;
    }

    public String getPoppinsBold() {
        return POPPINS_BOLD;
    }
}