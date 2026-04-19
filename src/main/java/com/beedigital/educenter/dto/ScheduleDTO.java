package com.beedigital.educenter.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    private Long   id;
    private String courseCode;
    private String courseLabel;
    private String teacherName;
    private Long   teacherId;
    private String groupName;
    private String date;        // "2024-03-18"
    private String startTime;   // "08:30"
    private String endTime;     // "10:15"
    private String room;
    private String type;        // CM, TD, TP
    private String session;     // JOUR, SOIR
    private Boolean isCancelled;
    private String cancelReason;
}

