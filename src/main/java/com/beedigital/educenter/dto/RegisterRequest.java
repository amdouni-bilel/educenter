package com.beedigital.educenter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @Email(message = "❌ Email invalide")
    @NotBlank(message = "❌ Email requis")
    private String email;

    @NotBlank(message = "❌ Mot de passe requis")
    @Size(min = 6, message = "❌ Mot de passe: minimum 6 caractères")
    // ✅ Regex assouplie — plus de contrainte sur les symboles
    private String password;

    @NotBlank(message = "❌ Prénom requis")
    private String firstName;

    @NotBlank(message = "❌ Nom requis")
    private String lastName;

    private String roleCode;
    private String phone;
    private String address;
    private String birthDate;
    private String cin;
    private String registrationStatus;
}