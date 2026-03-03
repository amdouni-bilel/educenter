package com.beedigital.educenter.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("TEACHER")
public class Teacher extends User {

    @Column(unique = true, length = 20)
    private String teacherId;

    @Column(length = 100)
    private String specialization;

    @Column(length = 50)
    private String department;

    @Column(length = 20)
    private String employmentStatus;

    private Double salary;

    @Column(columnDefinition = "TEXT")
    private String qualifications;
}