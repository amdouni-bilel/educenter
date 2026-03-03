package com.beedigital.educenter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @Email(message = "❌ Email invalide. Format: user@educenter.tn")
    @NotBlank(message = "❌ Email requis")
    private String email;
    @NotBlank(message = "❌ Mot de passe requis")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "❌ Mot de passe: minimum 8 caractères, 1 majuscule, 1 minuscule, 1 chiffre, 1 symbole (@$!%*?&)"
    )
    private String password;
    @NotBlank(message = "❌ Prénom requis")
    private String firstName;
    @NotBlank(message = "❌ Nom requis")
    private String lastName;
    @NotBlank(message = "❌ Rôle requis (STUDENT ou TEACHER)")
    private String roleCode;
    private String phone;
    private String address;
}
