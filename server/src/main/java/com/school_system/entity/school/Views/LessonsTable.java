package com.school_system.entity.school.Views;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.school_system.enums.school.ModulType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lessons_table")
public class LessonsTable {

    @Id
    private Long id; // Assuming id is part of the view
    @Column(nullable = true)
    private Long teacherId; // Assuming id is part of the view
    private ModulType modulType;
    private String teacher;
    @Column(nullable = true)
    private String students;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startAt;
    private String isSigned;


}
