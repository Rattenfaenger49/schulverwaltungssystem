package com.school_system.enums.authentication;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleName {
    SUPERADMIN ("SUPERADMIN"), ADMIN ("ADMIN"), SUPERVISOR("SUPERVISOR"),
    TEACHER("TEACHER"), STUDENT ("STUDENT"), PARENT("PARENT");

    private final String value;

    RoleName(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
