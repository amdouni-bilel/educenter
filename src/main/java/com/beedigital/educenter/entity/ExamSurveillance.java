package com.beedigital.educenter.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "exam_surveillances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSurveillance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private String examName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(length = 50)
    private String room;

    @Builder.Default
    private Boolean isConfirmed = false;
}