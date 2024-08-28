package com.school_system.enums.school;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LessonType {
    ONLINE("ONLINE"), PRESENTS_HOME("PRESENTS_HOME"), PRESENTS_SCHOOL("PRESENTS_SCHOOL"), OTHER("OTHER");
    private final String value;
    LessonType(String value){
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }
}
