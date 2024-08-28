package com.school_system.entity.security.converter;

import com.school_system.enums.authentication.RoleName;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;
@Slf4j
@Converter(autoApply = true)
public class RoleNameConverter implements AttributeConverter<RoleName, String> {

    @Override
    public String convertToDatabaseColumn(RoleName roleName) {
        if (roleName == null) {
            return null;
        }
        return roleName.getValue();
    }

    @Override
    public RoleName convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return Stream.of(RoleName.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
