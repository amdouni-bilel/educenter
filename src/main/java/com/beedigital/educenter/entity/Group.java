package com.beedigital.educenter.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "class_groups")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Group {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(nullable = false, length = 10)
    private String session;         // JOUR ou SOIR

    @Column(length = 10)
    private String level;           // L1, L2, L3, M1, M2 ← NOUVEAU

    @Column(length = 100)
    private String department;      // ex: Informatique ← NOUVEAU

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "study_program_id")
    private StudyProgram studyProgram;

    @Column(length = 10)
    private String academicYear;

    private Integer maxStudents;

    @Column(nullable = false) @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false) @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}