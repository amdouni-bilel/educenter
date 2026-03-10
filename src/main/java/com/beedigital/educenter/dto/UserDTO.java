package com.beedigital.educenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean isActive;
    private String avatarUrl;       // Photo de profil
    private String phoneNumber;     // Numéro de téléphone
    private String address;         // Adresse

    // ─── Nom complet ───────────────────────────────────────────────────────────
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // ─── Vérification des rôles ────────────────────────────────────────────────
    public boolean isSuperAdmin() {
        return role != null && role.equals("SUPER_ADMIN");
    }

    public boolean isAdmin() {
        return role != null && role.equals("ADMIN");
    }

    public boolean isRegistrar() {
        return role != null && role.equals("REGISTRAR");
    }

    public boolean isTeacher() {
        return role != null && role.equals("TEACHER");
    }

    public boolean isStudent() {
        return role != null && role.equals("STUDENT");
    }

    public boolean isParent() {
        return role != null && role.equals("PARENT");
    }

    // ─── Vérification accès admin (Super Admin OU Admin) ───────────────────────
    public boolean hasAdminAccess() {
        return isSuperAdmin() || isAdmin();
    }

    // ─── Initiales pour l'avatar par défaut ────────────────────────────────────
    public String getInitials() {
        String f = (firstName != null && !firstName.isEmpty()) ? String.valueOf(firstName.charAt(0)) : "";
        String l = (lastName != null && !lastName.isEmpty()) ? String.valueOf(lastName.charAt(0)) : "";
        return (f + l).toUpperCase();
    }
}