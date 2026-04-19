package com.beedigital.educenter.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @Column(nullable = false)
    private String label;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer coeff;          // ✅ Integer

    private Integer cmHours;
    private Integer tdHours;
    private Integer tpHours;

    @Column(length = 10)
    private String semester;

    @Column(length = 100)
    private String department;

    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}