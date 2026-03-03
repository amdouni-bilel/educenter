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
    public String getFullName() {
        return firstName + " " + lastName;
    }
    public boolean isSuperAdmin() {
        return role != null && role.equals("SUPER_ADMIN");
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
}