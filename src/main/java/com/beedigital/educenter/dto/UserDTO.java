package com.beedigital.educenter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    // ── Commun ────────────────────────────────────────────────────────────────
    private Long    id;
    private String  firstName;
    private String  lastName;
    private String  email;
    private String  phone;
    private String  address;
    private String  gender;
    private String  role;
    private Boolean isActive;
    private String  registrationStatus;
    private String  createdAt;
    private String  birthDate;
    private String  username;

    // ── Étudiant ──────────────────────────────────────────────────────────────
    private String  studentId;       // matricule STU001
    private String  cin;
    private String  groupName;       // ex: GLSI-1
    private String  level;           // L1, L2, L3, M1, M2
    private String  studyProgram;    // ex: Licence Informatique
    private Integer enrollmentYear;

    // ── Enseignant ────────────────────────────────────────────────────────────
    private String  teacherId;
    private String  department;      // ex: Informatique
    private String  specialization;  // ex: Mathématiques, Algorithmique
    private String  subjects;        // matières enseignées (liste CSV)

    // ── Parent ────────────────────────────────────────────────────────────────
    private String  childName;
}