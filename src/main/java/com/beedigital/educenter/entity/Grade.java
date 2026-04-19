package com.beedigital.educenter.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private String evaluationType;  // DS, EXAM, TP
    private Double value;           // note /20
    private Double coefficient;

    @Builder.Default
    private Boolean isValidated = false;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public Boolean getIsValidated() { return isValidated; }
    public Double getValue()        { return value; }
    public Double getCoefficient()  { return coefficient; }
}