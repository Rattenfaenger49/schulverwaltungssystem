package com.school_system.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.school_system.entity.school.Contact;
import com.school_system.entity.school.Institution;
import com.school_system.enums.school.ContractStatus;
import com.school_system.enums.school.ContractType;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractResponse {
    private Long id;
    private String contractNumber;
    private String referenceContractNumber;
    private ContractType contractType;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startAt;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate  endAt;
    private ContractStatus status;
    private ContractStudentResponse student;
    @Builder.Default
    private List<ModulResponse> moduls = new ArrayList<>();
    private Contact contact;
    private Institution institution;
    private String comment;



}
