package com.beedigital.educenter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSubjectDTO {

    // ─── Enseignant ───────────────────────────────────────────────────────────
    private Long   teacherId;
    private String teacherFirstName;
    private String teacherLastName;
    private String teacherEmail;

    // ─── Matière ──────────────────────────────────────────────────────────────
    private Long    subjectId;
    private String  subjectCode;
    private String  subjectName;
    private String  semester;
    private Integer coefficient;
}