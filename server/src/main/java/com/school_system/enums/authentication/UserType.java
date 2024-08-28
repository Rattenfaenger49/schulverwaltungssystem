package com.school_system.enums.authentication;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {
    ADMIN("ADMIN"), STUDENT("STUDENT"), SUPERVISOR("SUPERVISOR"), TEACHER("TEACHER");

    private final String value;

    UserType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
