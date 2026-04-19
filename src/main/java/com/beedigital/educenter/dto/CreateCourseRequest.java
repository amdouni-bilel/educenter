package com.beedigital.educenter.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCourseRequest {

    @NotBlank(message = "Code requis")
    private String code;

    @NotBlank(message = "Intitulé requis")
    private String label;

    private String description;

    @NotNull(message = "Coefficient requis")
    @Min(value = 1, message = "Coefficient minimum : 1")
    @Max(value = 10, message = "Coefficient maximum : 10")
    private Integer coeff;

    private Integer cmHours;
    private Integer tdHours;
    private Integer tpHours;
    private String semester;
    private String department;
}