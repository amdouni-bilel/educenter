package com.beedigital.educenter.service;

import com.beedigital.educenter.dto.UserDTO;
import com.beedigital.educenter.dto.CreateUserRequest;
import com.beedigital.educenter.entity.User;
import com.beedigital.educenter.entity.Role;
import com.beedigital.educenter.entity.Teacher;
import com.beedigital.educenter.enums.RoleEnum;
import com.beedigital.educenter.repositories.UserRepository;
import com.beedigital.educenter.repositories.RoleRepository;
import com.beedigital.educenter.repositories.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService - Service pour la gestion des utilisateurs (CRUD)
 *
 * PERMISSIONS:
 * ✅ SUPER_ADMIN : Peut créer TOUS les rôles
 * ✅ REGISTRAR   : Peut créer STUDENT, TEACHER
 * ❌ TEACHER     : Ne peut créer personne
 * ❌ STUDENT     : Ne peut créer personne
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeacherRepository teacherRepository;

    // ─── Créer un utilisateur ──────────────────────────────────────────────────
    public UserDTO createUser(CreateUserRequest request, String creatorRole) throws Exception {

        // 1. Email unique ?
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new Exception("Cet email est déjà utilisé");
        }

        // 2. Rôle valide ?
        RoleEnum targetRole;
        try {
            targetRole = RoleEnum.valueOf(request.getRoleCode());
        } catch (IllegalArgumentException e) {
            throw new Exception("Rôle invalide: " + request.getRoleCode());
        }

        // 3. Permissions du créateur
        checkPermissionToCreateRole(creatorRole, targetRole);

        // 4. Récupérer le rôle en base
        Role role = roleRepository.findByCode(targetRole);
        if (role == null) {
            throw new Exception("Rôle non trouvé en base de données");
        }

        // 5. Créer l'utilisateur
        User newUser = User.builder()
                .username(request.getEmail().split("@")[0])
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(role)
                .isActive(true)
                .registrationStatus("APPROVED")
                .build();

        return convertToDTO(userRepository.save(newUser));
    }

    // ─── Lister tous les utilisateurs ─────────────────────────────────────────
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ─── Récupérer par ID ──────────────────────────────────────────────────────
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        return convertToDTO(user);
    }

    // ─── Récupérer par email (pour /me) ── AJOUTÉ ─────────────────────────────
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("Utilisateur non trouvé avec l'email : " + email);
        }
        return convertToDTO(user);
    }

    // ─── Modifier un utilisateur ── AJOUTÉ ────────────────────────────────────
    public UserDTO updateUser(Long id, CreateUserRequest request) throws Exception {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + id));

        // Vérifier email unique si changé
        if (!existing.getEmail().equals(request.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()) != null) {
                throw new Exception("Cet email est déjà utilisé");
            }
            existing.setEmail(request.getEmail());
            existing.setUsername(request.getEmail().split("@")[0]);
        }

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setPhone(request.getPhone());
        existing.setAddress(request.getAddress());

        // Changer le mot de passe seulement si fourni
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return convertToDTO(userRepository.save(existing));
    }

    // ─── Activer / Désactiver un utilisateur ── AJOUTÉ ────────────────────────
    public UserDTO toggleStatus(Long id, Boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + id));

        // Impossible de désactiver un SUPER_ADMIN
        if (user.getRole().getCode() == RoleEnum.SUPER_ADMIN && !isActive) {
            throw new SecurityException("Impossible de désactiver le SUPER_ADMIN");
        }

        user.setIsActive(isActive);
        return convertToDTO(userRepository.save(user));
    }

    // ─── Supprimer un utilisateur (sans rôle en paramètre) ── AJOUTÉ ──────────
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + id));

        if (user.getRole().getCode() == RoleEnum.SUPER_ADMIN) {
            throw new SecurityException("Impossible de supprimer le SUPER_ADMIN");
        }

        userRepository.delete(user);
    }

    // ─── Supprimer avec vérification du rôle (ancienne version gardée) ─────────
    public void deleteUser(Long id, String deleterRole) throws Exception {
        if (!deleterRole.equals("SUPER_ADMIN")) {
            throw new Exception("Seul SUPER_ADMIN peut supprimer des utilisateurs");
        }
        deleteUser(id);
    }

    // ─── Lister les enseignants ────────────────────────────────────────────────
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    // ─── Récupérer un enseignant par ID ───────────────────────────────────────
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enseignant non trouvé avec l'ID : " + id));
    }

    // ─── Modifier un enseignant ────────────────────────────────────────────────
    public Teacher updateTeacher(Long id, Teacher updated) {
        Teacher existing = getTeacherById(id);
        existing.setSpecialization(updated.getSpecialization());
        existing.setDepartment(updated.getDepartment());
        existing.setEmploymentStatus(updated.getEmploymentStatus());
        existing.setSalary(updated.getSalary());
        existing.setQualifications(updated.getQualifications());
        return teacherRepository.save(existing);
    }

    // ─── Supprimer un enseignant ───────────────────────────────────────────────
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new EntityNotFoundException("Enseignant non trouvé avec l'ID : " + id);
        }
        teacherRepository.deleteById(id);
    }

    // ─── Vérification des permissions de création ─────────────────────────────
    private void checkPermissionToCreateRole(String creatorRole, RoleEnum targetRole) throws Exception {
        switch (creatorRole) {
            case "SUPER_ADMIN" -> { return; }
            case "REGISTRAR" -> {
                if (targetRole == RoleEnum.STUDENT || targetRole == RoleEnum.TEACHER) return;
                throw new Exception("Agent de Scolarité peut créer uniquement : STUDENT, TEACHER");
            }
            case "TEACHER" -> throw new Exception("Enseignant ne peut créer personne");
            case "STUDENT" -> throw new Exception("Étudiant ne peut créer personne");
            default -> throw new Exception("Rôle de créateur invalide : " + creatorRole);
        }
    }

    // ─── Convertir User → UserDTO (sans password) ─────────────────────────────
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().getCode().toString())
                .isActive(user.getIsActive())
                .phoneNumber(user.getPhone())
                .address(user.getAddress())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}