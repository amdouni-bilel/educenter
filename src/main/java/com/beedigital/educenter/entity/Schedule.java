package com.beedigital.educenter.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(length = 50)
    private String room;

    @Column(length = 10)
    private String type;            // CM, TD, TP, EXAM

    @Column(length = 20)
    private String groupName;

    @Column(length = 10)
    private String semester;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCancelled = false;

    @Column(columnDefinition = "TEXT")
    private String cancelReason;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}