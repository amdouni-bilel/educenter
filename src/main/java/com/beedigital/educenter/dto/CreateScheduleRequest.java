package com.beedigital.educenter.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScheduleRequest {

    @NotNull(message = "Cours requis")
    private Long courseId;

    @NotNull(message = "Enseignant requis")
    private Long teacherId;

    @NotBlank(message = "Groupe requis")
    private String groupName;

    @NotBlank(message = "Date requise")
    private String date;        // "2024-03-18"

    @NotBlank(message = "Heure début requise")
    private String startTime;   // "08:30"

    @NotBlank(message = "Heure fin requise")
    private String endTime;     // "10:15"

    private String room;

    @NotBlank(message = "Type requis")
    private String type;        // CM, TD, TP

    private String semester;
}