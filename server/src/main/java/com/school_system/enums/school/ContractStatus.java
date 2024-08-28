package com.school_system.enums.school;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ContractStatus {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    TERMINATED("TERMINATED"),
    BLOCKED("BLOCKED"),
    IN_PROGRESS("IN_PROGRESS"),
    UNKNOWN("UNKNOWN"),
    ANY("Alle");

    private final String value;
    ContractStatus(String value){

        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }
}
