package com.beedigital.educenter.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupDTO {
    private Long    id;
    private String  name;
    private String  level;
    private String  session;
    private String  department;
    private String  academicYear;
    private Integer maxStudents;
    private Integer studentCount;
    private Boolean isActive;
    private String  createdAt;
    private Long    studyProgramId;
    private String  studyProgramName;
}