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
    private String studentId;       // STU001

    @Column(unique = true, length = 8)
    private String cin;

    @Column(length = 20)
    private String groupName;       // ex: GLSI-1

    @Column(length = 10)
    private String level;           // L1, L2, L3, M1, M2 ← NOUVEAU

    @Column(length = 100)
    private String studyProgram;    // ex: Licence Informatique ← NOUVEAU

    private Integer enrollmentYear;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private Boolean isTransferred;
}