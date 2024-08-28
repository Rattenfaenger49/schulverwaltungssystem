package com.school_system.enums.school;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StorageType {
    LOCAL("LOCAL"), GCP_Bucket("GCP_Bucket"), AWS("AWS"), AZURE("AZURE");

    private final String value;

    StorageType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
