package com.beedigital.educenter.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "absences")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Absence {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(length = 100)
    private String studentName;

    @Column(length = 30)
    private String groupName;

    @Column(nullable = false, length = 100)
    private String courseLabel;

    @Column(nullable = false, length = 10)
    private String date;            // "2024-03-18"

    @Column(length = 5)
    private String type;            // CM, TD, TP

    @Column(nullable = false)
    @Builder.Default
    private Boolean isJustified = false;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}