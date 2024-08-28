package com.school_system.entity.school;



import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.school_system.enums.school.LessonDuration;
import com.school_system.enums.school.ModulType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "moduls",  uniqueConstraints = {
        @UniqueConstraint(columnNames = {"contract_id", "modul"})
})
public class Modul {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModulType modulType;

    @Column(nullable = false)
    private Double units;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Unterrichtdauer ist Pflichtfeld")
    private LessonDuration lessonDuration;
    @Column
    private BigDecimal singleLessonCost;
    @Column
    private BigDecimal groupLessonCost;

    private boolean singleLessonAllowed;

    private boolean groupLessonAllowed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id",nullable = false)
    @ToString.Exclude
    private Contract contract;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
    private String updatedBy;

}
