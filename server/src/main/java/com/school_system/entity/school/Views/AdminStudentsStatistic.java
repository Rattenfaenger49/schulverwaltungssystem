package com.school_system.entity.school.Views;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;


// This class represents the AdminStatistic View in the database
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Table(name = "admin_students_statistic")
public class AdminStudentsStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Dummy primary key
    private Integer studentsNumber;
    private Integer markedForDeletionStudents;



}
