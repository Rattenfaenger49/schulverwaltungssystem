package com.school_system.entity.school;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.school_system.entity.security.Student;
import com.school_system.entity.security.Teacher;
import com.school_system.enums.school.ContractType;
import com.school_system.enums.school.LessonType;
import com.school_system.enums.school.ModulType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "lessons_versions")
@EntityListeners(AuditingEntityListener.class)
public class LessonVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private long lessonId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModulType modulType;

    @Column(nullable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startAt;
    @Column(nullable = false)
    private Double units;
    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false) // Adjust the length as needed
    private LessonType lessonType;
    @Column(length = 512, nullable = false)
    @NonNull
    private String description;
    @Column(length = 512)
    private String comment;
    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    private String studentsIds;
    private Long teacherId;
    private String signaturesIds;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @CreatedDate
    private LocalDateTime createdAt;
    @CreatedBy
    private String createdBy;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        LessonVersion lesson = (LessonVersion) o;
        return getId() != null && Objects.equals(getId(), lesson.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
