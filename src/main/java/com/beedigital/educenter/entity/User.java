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
    private Long id; //string
    //genre nationalité

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

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isSuperAdmin() {
        return role != null && role.getCode() == RoleEnum.SUPER_ADMIN;
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

    public boolean isPending() {
        return registrationStatus != null && registrationStatus.equals("PENDING");
    }

    public boolean isApproved() {
        return registrationStatus != null && registrationStatus.equals("APPROVED");
    }

    public boolean canLogin() {
        return isActive && isApproved();
    }
}
