package com.beedigital.educenter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyProgramDTO {
    private Long id;
    private String code;
    private String name;
    private String type;       // LICENCE, MASTER, DOCTORAT, BTS
    private String level;      // L1, L2, L3, M1, M2
    private String department;
    private String academicYear;
    private String description;
    private Boolean isActive;
}
