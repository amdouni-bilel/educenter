package com.beedigital.educenter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @Email(message = "❌ Email invalide. Exemple: user@educenter.tn")
    @NotBlank(message = "❌ Email requis")
    private String email;
    @NotBlank(message = "❌ Mot de passe requis")
    private String password;
}
