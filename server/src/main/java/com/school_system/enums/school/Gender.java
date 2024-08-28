package com.school_system.enums.school;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender  {
    MALE("männlich") , FEMALE ("weiblich"), UNKNOWN("unbekannt");

    private final String value;
    Gender(String value){
        this.value = value;
    }
    @JsonValue
    public String getValue(){
        return value;
    }

}
