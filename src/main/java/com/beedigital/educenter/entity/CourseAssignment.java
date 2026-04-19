package com.beedigital.educenter.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Affectation : qui enseigne quel cours a quel groupe
 * Ex: Prof. Mansouri enseigne MATH-301 au groupe L1-A
 */
@Entity
@Table(name = "course_assignments",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"teacher_id", "course_id", "group_id", "academic_year"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;          // Course au lieu de Subject

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(length = 10)
    private String academicYear;

    @Column(nullable = false) @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false) @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}