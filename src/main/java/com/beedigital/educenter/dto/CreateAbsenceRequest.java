package com.beedigital.educenter.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateAbsenceRequest {
    @NotNull(message = "Étudiant requis")
    private Long    studentId;
    private String  studentName;
    private String  groupName;
    @NotBlank(message = "Cours requis")
    private String  courseLabel;
    private String  course;         // alias
    private String  courseName;     // alias
    @NotBlank(message = "Date requise")
    private String  date;
    private String  type;           // CM, TD, TP
    private Boolean isJustified;
    private Boolean justified;      // alias
    private String  justification;
}
