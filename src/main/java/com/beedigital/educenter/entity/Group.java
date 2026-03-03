package com.beedigital.educenter.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String label;

    private Integer capacity;

    @Builder.Default
    private Integer enrolledCount = 0;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "academic_year_id")
    private AcademicYear academicYear;
}