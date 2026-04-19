package com.beedigital.educenter.dto;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseDTO {
    private Long    id;
    private String  code;
    private String  label;
    private String  description;
    private Integer coeff;        // Double — comme dans Course.java
    private Integer cmHours;
    private Integer tdHours;
    private Integer tpHours;
    private String  semester;
    private String  department;
    private Boolean isActive;
    private String  createdAt;
}