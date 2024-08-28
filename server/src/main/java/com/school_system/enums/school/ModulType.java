package com.school_system.enums.school;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ModulType {
    MATHEMATIK("Mathematik"), PHYSIK("Physik"),
    DEUTSCH("Deutsch"), FRANZOESISCH("Französisch"),
    ENGLISCH("Englisch"), BIOLOGIE("Biologie"),
    CHEMIE("Chemie"), INFORMATIK("Informatik"),
    SPANISCH("Spanisch"), GESCHICHTE("Geschichte"),
    INFORMATIC("Informatik"), MUSIC("MUSIK"),
    GEOGRAPHIE("Geographie"), NOT_LISTED("Nicht gelistet") ;
    private final String value;
    ModulType(String value){
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }

}
