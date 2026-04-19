package com.beedigital.educenter.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGroupRequest {

    @NotBlank(message = "Nom du groupe requis")
    private String name;

    @NotBlank(message = "Niveau requis")
    private String level;

    private String department;
    private String academicYear;
    private String session;         // JOUR ou SOIR

    @Min(value = 1, message = "Capacite minimum : 1")
    @Max(value = 100, message = "Capacite maximum : 100")
    private Integer maxStudents;
}