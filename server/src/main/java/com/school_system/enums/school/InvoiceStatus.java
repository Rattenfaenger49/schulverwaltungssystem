package com.school_system.enums.school;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InvoiceStatus {
    PENDING_APPROVAL("Wartet auf Freigabe"),
    UNDER_REVIEW("In Bearbeitung"),
    ACCEPTED("Akzeptiert"),
    REJECTED("Abgelehnt"),
    PAID("Bezahlt");

    private final String value;
    InvoiceStatus(String value){
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }
}

