package com.beedigital.educenter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudyProgramRequest {

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "Le type est obligatoire")
    private String type;        // LICENCE, MASTER, DOCTORAT, BTS

    private String level;       // L1, L2, L3, M1, M2
    private String department;
    private String academicYear;
    private String description;
}