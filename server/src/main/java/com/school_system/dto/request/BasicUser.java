package com.school_system.dto.request;

import java.util.Objects;

public record BasicUser(Long id, String firstName, String lastName, String email) {
    public BasicUser {
        Objects.requireNonNull(id);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
    }
}
