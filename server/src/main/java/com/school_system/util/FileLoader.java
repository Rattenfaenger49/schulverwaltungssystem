package com.school_system.util;

import com.school_system.config.MyAppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class FileLoader {



    public static String loadEmailTemplate(String tempalte) {
        // Load the email template from resources
        // Here, we are using a simple example of reading the file content
        File file = new File( tempalte);
        if (!file.exists()) {
            throw new RuntimeException("Template not found: " + tempalte);
        }

        try (InputStream is = new FileInputStream(file)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to load email template from path: {}", tempalte, e);
            throw new RuntimeException("Failed to load email template", e);
        }
    }
}
