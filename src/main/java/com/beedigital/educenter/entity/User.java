package com.beedigital.educenter.entity;

import com.beedigital.educenter.enums.RoleEnum;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @ToString.Exclude
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    // ─── Ajoutés ici car communs à tous les utilisateurs ───────────────────────
    @Column(length = 10)
    private String gender;          // M, F, AUTRE

    @Column(length = 100)
    private String nationality;     // Tunisienne, Française, etc.
    // ───────────────────────────────────────────────────────────────────────────

    @Column(length = 500)
    private String avatarUrl;       // Photo de profil

    @Column(length = 500)
    private String verificationToken;

    @Column(length = 500)
    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private String registrationStatus = "APPROVED";

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;

    // ─── Méthodes utilitaires ──────────────────────────────────────────────────
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isSuperAdmin() {
        return role != null && role.getCode() == RoleEnum.SUPER_ADMIN;
    }

    public boolean isAdmin() {
        return role != null && role.getCode() == RoleEnum.REGISTRAR;
    }

    public boolean isRegistrar() {
        return role != null && role.getCode() == RoleEnum.REGISTRAR;
    }

    public boolean isTeacher() {
        return role != null && role.getCode() == RoleEnum.TEACHER;
    }

    public boolean isStudent() {
        return role != null && role.getCode() == RoleEnum.STUDENT;
    }

    public boolean isParent() {
        return role != null && role.getCode() == RoleEnum.PARENT;
    }

    public boolean hasAdminAccess() {
        return isSuperAdmin() || isAdmin();
    }

    public boolean isPending() {
        return registrationStatus != null && registrationStatus.equals("PENDING");
    }

    public boolean isApproved() {
        return registrationStatus != null && registrationStatus.equals("APPROVED");
    }

    public boolean canLogin() {
        return isActive && isApproved();
    }

    public String getInitials() {
        String f = (firstName != null && !firstName.isEmpty()) ? String.valueOf(firstName.charAt(0)) : "";
        String l = (lastName != null && !lastName.isEmpty()) ? String.valueOf(lastName.charAt(0)) : "";
        return (f + l).toUpperCase();
    }
}