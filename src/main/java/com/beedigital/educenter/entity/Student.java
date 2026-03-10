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



    private Integer enrollmentYear;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private Boolean isTransferred;
}