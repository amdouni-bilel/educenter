package com.beedigital.educenter.service;

import com.beedigital.educenter.dto.UserDTO;
import com.beedigital.educenter.dto.CreateUserRequest;
import com.beedigital.educenter.entity.*;
import com.beedigital.educenter.enums.RoleEnum;
import com.beedigital.educenter.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository    userRepository;
    private final RoleRepository    roleRepository;
    private final PasswordEncoder   passwordEncoder;
    private final TeacherRepository teacherRepository;

    // ─── Créer un utilisateur ─────────────────────────────────────────────────
    public UserDTO createUser(CreateUserRequest request, String creatorRole) throws Exception {
        if (userRepository.findByEmail(request.getEmail()) != null)
            throw new Exception("Email déjà utilisé : " + request.getEmail());

        RoleEnum roleEnum = RoleEnum.valueOf(request.getRoleCode());
        checkPermission(creatorRole, roleEnum);

        Role role = roleRepository.findByCode(roleEnum);
        if (role == null) throw new Exception("Rôle non trouvé : " + request.getRoleCode());

        User user = switch (roleEnum) {
            case STUDENT -> {
                Student s = new Student();
                s.setCin(request.getCin());
                s.setLevel(request.getLevel());
                s.setStudyProgram(request.getStudyProgram());
                s.setEnrollmentYear(request.getEnrollmentYear());
                yield s;
            }
            case TEACHER -> {
                Teacher t = new Teacher();
                t.setDepartment(request.getDepartment());
                t.setSpecialization(request.getSpecialization());
                yield t;
            }
            default -> new User();
        };

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getEmail().split("@")[0]);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        if (request.getBirthDate() != null && !request.getBirthDate().isBlank()) {
            try { user.setBirthDate(request.getBirthDate()); } catch(Exception ignored) {}
        }
        user.setRole(role);
        user.setIsActive(true);
        user.setRegistrationStatus("APPROVED");

        return toDTO(userRepository.save(user));
    }

    // ─── Lister tous — exclut PENDING et REJECTED ────────────────────────────
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u -> !"PENDING".equals(u.getRegistrationStatus())
                        && !"REJECTED".equals(u.getRegistrationStatus()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Par ID ───────────────────────────────────────────────────────────────
    public UserDTO getUserById(Long id) {
        return toDTO(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé : " + id)));
    }

    // ─── Par email ────────────────────────────────────────────────────────────
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new EntityNotFoundException("Utilisateur non trouvé : " + email);
        return toDTO(user);
    }

    // ─── Modifier ─────────────────────────────────────────────────────────────
    public UserDTO updateUser(Long id, CreateUserRequest request) throws Exception {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé : " + id));

        if (!existing.getEmail().equals(request.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()) != null)
                throw new Exception("Cet email est déjà utilisé");
            existing.setEmail(request.getEmail());
            existing.setUsername(request.getEmail().split("@")[0]);
        }
        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setPhone(request.getPhone());
        existing.setAddress(request.getAddress());
        if (request.getPassword() != null && !request.getPassword().isBlank())
            existing.setPassword(passwordEncoder.encode(request.getPassword()));

        if (existing instanceof Student student) {
            if (request.getCin()            != null) student.setCin(request.getCin());
            if (request.getLevel()          != null) student.setLevel(request.getLevel());
            if (request.getStudyProgram()   != null) student.setStudyProgram(request.getStudyProgram());
            if (request.getEnrollmentYear() != null) student.setEnrollmentYear(request.getEnrollmentYear());
        }
        if (existing instanceof Teacher teacher) {
            if (request.getDepartment()     != null) teacher.setDepartment(request.getDepartment());
            if (request.getSpecialization() != null) teacher.setSpecialization(request.getSpecialization());
        }

        return toDTO(userRepository.save(existing));
    }

    // ─── Activer / Désactiver ─────────────────────────────────────────────────
    public UserDTO toggleStatus(Long id, Boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé : " + id));
        if (user.getRole().getCode() == RoleEnum.SUPER_ADMIN && !isActive)
            throw new SecurityException("Impossible de désactiver le SUPER_ADMIN");
        user.setIsActive(isActive);
        return toDTO(userRepository.save(user));
    }

    // ─── Supprimer ────────────────────────────────────────────────────────────
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé : " + id));
        if (user.getRole().getCode() == RoleEnum.SUPER_ADMIN)
            throw new SecurityException("Impossible de supprimer le SUPER_ADMIN");
        userRepository.delete(user);
    }

    // ─── Enseignants ──────────────────────────────────────────────────────────
    public List<Teacher> getAllTeachers() { return teacherRepository.findAll(); }

    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enseignant non trouvé : " + id));
    }

    public Teacher updateTeacher(Long id, Teacher updated) {
        Teacher existing = getTeacherById(id);
        existing.setSpecialization(updated.getSpecialization());
        existing.setDepartment(updated.getDepartment());
        return teacherRepository.save(existing);
    }

    // ─── Inscriptions en attente ──────────────────────────────────────────────
    public List<UserDTO> getPendingUsers() {
        return userRepository.findByRegistrationStatus("PENDING").stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO approveRegistration(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé : " + id));
        user.setRegistrationStatus("APPROVED");
        user.setIsActive(true);
        if (user instanceof Student student) {
            long count = userRepository.countByRole_Code(RoleEnum.STUDENT);
            student.setStudentId("STU" + String.format("%03d", count + 1));
        }
        return toDTO(userRepository.save(user));
    }

    // ─── ✅ FIX : Rejeter = supprimer complètement ───────────────────────────
    public void rejectRegistration(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé : " + id));
        // Suppression complète — l'utilisateur ne doit pas rester dans le système
        userRepository.delete(user);
    }

    // ─── Vérification permissions ─────────────────────────────────────────────
    private void checkPermission(String creatorRole, RoleEnum targetRole) throws Exception {
        switch (creatorRole) {
            case "SUPER_ADMIN" -> { return; }
            case "REGISTRAR"   -> {
                if (targetRole == RoleEnum.STUDENT || targetRole == RoleEnum.TEACHER) return;
                throw new Exception("REGISTRAR peut créer uniquement STUDENT ou TEACHER");
            }
            default -> throw new Exception("Non autorisé à créer des utilisateurs");
        }
    }

    // ─── toDTO — unique, complète ─────────────────────────────────────────────
    private UserDTO toDTO(User user) {
        UserDTO.UserDTOBuilder dto = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .gender(user.getGender())
                .role(user.getRole() != null ? user.getRole().getCode().toString() : null)
                .isActive(user.getIsActive())
                .registrationStatus(user.getRegistrationStatus())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .birthDate(user.getBirthDate() != null ? user.getBirthDate().toString() : null);

        if (user instanceof Student s) {
            dto.studentId(s.getStudentId())
                    .cin(s.getCin())
                    .groupName(s.getGroupName())
                    .level(s.getLevel())
                    .studyProgram(s.getStudyProgram())
                    .enrollmentYear(s.getEnrollmentYear());
        }
        if (user instanceof Teacher t) {
            dto.department(t.getDepartment())
                    .specialization(t.getSpecialization());
        }

        return dto.build();
    }
}