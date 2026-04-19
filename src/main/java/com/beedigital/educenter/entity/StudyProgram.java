package com.beedigital.educenter.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Filière / Programme d'études
 * Ex: "Licence Informatique", "Master Génie Logiciel", "Licence Mathématiques"
 */
@Entity
@Table(name = "study_programs")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StudyProgram {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String code;            // ex: LIC-INFO, MAST-GL

    @Column(nullable = false, length = 100)
    private String name;            // ex: Licence Informatique

    @Column(nullable = false, length = 20)
    private String type;            // LICENCE, MASTER, DOCTORAT, BTS

    @Column(length = 20)
    private String level;           // L1,L2,L3 ou M1,M2 (niveau dans le cycle)

    @Column(length = 100)
    private String department;      // ex: Département Informatique

    @Column(length = 10)
    private String academicYear;    // ex: 2024-2025

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false) @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false) @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}