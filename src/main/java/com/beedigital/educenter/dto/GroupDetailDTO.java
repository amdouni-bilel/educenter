package com.beedigital.educenter.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDetailDTO {

    // ─── Infos du groupe ──────────────────────────────────────────────────────
    private Long    id;
    private String  name;
    private String  session;
    private String  academicYear;
    private Integer maxStudents;
    private Integer studentCount;
    private Boolean isActive;

    // ─── Filière liée ─────────────────────────────────────────────────────────
    private Long   studyProgramId;
    private String studyProgramName;
    private String studyProgramType;

    // ─── Étudiants paginés ────────────────────────────────────────────────────
    private List<StudentSummaryDTO> students;
    private int  studentPage;
    private int  studentTotalPages;
    private long studentTotalCount;

    // ─── Enseignants + matières ───────────────────────────────────────────────
    private List<TeacherSubjectDTO> teacherSubjects;
}