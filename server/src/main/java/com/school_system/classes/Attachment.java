package com.school_system.classes;

import org.springframework.core.io.InputStreamSource;



public record Attachment(String filename, InputStreamSource source) {
}