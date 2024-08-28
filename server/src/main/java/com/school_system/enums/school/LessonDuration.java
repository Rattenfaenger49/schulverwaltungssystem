package com.school_system.enums.school;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LessonDuration {
    MINUTES_45("45 min"),
    MINUTES_60("60 min");
    private final String value;
    LessonDuration(String value){
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }
}
