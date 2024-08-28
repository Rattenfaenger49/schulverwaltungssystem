package com.school_system.dto.request;

import com.school_system.enums.school.FileCategory;
// Id is userId or entityId like lessonId
public record FileMetaDataRequest(FileCategory fileCategory, Long id) {
}
