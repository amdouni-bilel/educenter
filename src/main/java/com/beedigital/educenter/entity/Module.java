package com.beedigital.educenter.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "modules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String label;

    private Double coefficient;
    private Double volumeHoursCM;
    private Double volumeHoursTD;
    private Double volumeHoursTP;

    @Column(columnDefinition = "TEXT")
    private String description;
}
