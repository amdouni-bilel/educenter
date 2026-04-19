package com.beedigital.educenter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDTO {
    private Long    id;
    private String  code;
    private String  name;
    private String  semester;
    private Integer coefficient;  // Integer comme dans l'entite Subject
    private Integer cmHours;
    private Integer tdHours;
    private Integer tpHours;
    private String  department;
    private String  description;
    private Boolean isActive;
}