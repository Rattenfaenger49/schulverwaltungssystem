package com.school_system.enums.authentication;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TokenType {
    ACCESS_TOKEN("ACCESS_TOKEN"), REFRESH_TOKEN("REFRESH_TOKEN");

    private final String value;
    TokenType(String value){
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }
}
