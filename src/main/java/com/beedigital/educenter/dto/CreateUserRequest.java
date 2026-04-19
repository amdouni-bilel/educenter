package com.beedigital.educenter.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    // ── Commun ─────────────────────────────────────────────────────────────
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank @Email private String email;
    @NotBlank private String password;
    @NotBlank private String roleCode;  // STUDENT, TEACHER, PARENT, REGISTRAR, SUPER_ADMIN
    private String phone;
    private String gender;
    private String address;
    private String birthDate;

    // ── Étudiant ───────────────────────────────────────────────────────────
    private String  cin;
    private String  level;          // L1, L2, L3, M1, M2
    private String  studyProgram;   // ex: Licence Informatique
    private Integer enrollmentYear;

    // ── Enseignant ─────────────────────────────────────────────────────────
    private String  department;     // ex: Informatique
    private String  specialization; // ex: Mathématiques
}