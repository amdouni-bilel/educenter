package com.beedigital.educenter.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("STUDENT")
public class Student extends User {

    @Column(unique = true, length = 20)
    private String studentId;

    @Column(length = 100)
    private String matricule;

    @Column(length = 50)
    private String gender;

    @Column(length = 100)
    private String nationality;

    private Integer enrollmentYear;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private Boolean isTransferred;
}