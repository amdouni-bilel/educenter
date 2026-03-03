package com.beedigital.educenter.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalTime;

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

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(length = 50)
    private String room;

    private String type;
}
