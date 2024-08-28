package com.school_system.dto.response;

import com.school_system.entity.school.FileMetadata;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileWithMetadata {
    byte[] file;
    FileMetadata fileMetadata;
    InvoiceResponse invoice;

}
