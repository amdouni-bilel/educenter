package com.beedigital.educenter.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AbsenceDTO {
    private Long    id;
    private Long    studentId;
    private String  studentName;
    private String  groupName;
    private String  courseLabel;
    private String  date;
    private String  type;           // CM, TD, TP
    private Boolean isJustified;
    private String  justification;
    private String  createdAt;
}