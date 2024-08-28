package com.school_system.enums.school;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FileCategory {
    // option all to simple get all contract for filters
    Other("Andere"), INVOICE("Rechnung"),DOCUMENTATION("Dokumentation"), HOMEWORK("Hausaufgabe"),PERSONAL_FILE("Persönlich"), LOGO("LOGO");
    private final String value;
    FileCategory(String value){
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }
}
