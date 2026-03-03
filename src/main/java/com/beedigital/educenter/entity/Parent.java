package com.beedigital.educenter.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "parents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("PARENT")
public class Parent extends User {

    @Column(unique = true, length = 20)
    private String parentId;

    @Column(length = 50)
    private String relationship;  // Mère, Père, Tuteur, etc
}