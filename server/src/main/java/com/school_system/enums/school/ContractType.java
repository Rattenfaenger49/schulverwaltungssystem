package com.school_system.enums.school;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ContractType {
    // option all to simple get all contract for filters
    WEEK("wöchentlich"), MONTH("monatlich"), PERIOD("zeitraum"), INDIVIDUALLY("individuell"), ANY("alle");
    private final String value;
    ContractType(String value){
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
    public static ContractType fromValue(String value) {
        for (ContractType type : ContractType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
