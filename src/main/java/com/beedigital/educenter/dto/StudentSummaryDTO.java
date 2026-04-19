package com.beedigital.educenter.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSummaryDTO {

    private Long      id;
    private String    studentId;
    private String    firstName;
    private String    lastName;
    private String    email;
    private String    phone;
    private String    cin;
    private LocalDate birthDate;
    private String    groupName;
    private Boolean   isActive;
    private String    registrationStatus;
}