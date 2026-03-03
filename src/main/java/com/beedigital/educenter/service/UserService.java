package com.beedigital.educenter.service;

import com.beedigital.educenter.dto.UserDTO;
import com.beedigital.educenter.dto.CreateUserRequest;
import com.beedigital.educenter.entity.User;
import com.beedigital.educenter.entity.Role;
import com.beedigital.educenter.enums.RoleEnum;
import com.beedigital.educenter.repositories.UserRepository;
import com.beedigital.educenter.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService - Service pour la gestion des utilisateurs (CRUD)
 *
 * PERMISSIONS:
 * ✅ SUPER_ADMIN: Peut créer TOUS (SUPER_ADMIN, REGISTRAR, TEACHER, STUDENT)
 * ✅ REGISTRAR: Peut créer STUDENT, TEACHER
 * ❌ TEACHER: Ne peut créer personne
 * ❌ STUDENT: Ne peut créer personne
 *
 * @author Équipe Développement
 * @version 2.0
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * CREATE USER - Créer un nouvel utilisateur par admin
     *
     * Vérifications:
     * 1. Email unique?
     * 2. Password fort?
     * 3. Rôle existe?
     * 4. Permissions du créateur OK?
     *
     * @param request Données du nouvel utilisateur
     * @param creatorRole Rôle de la personne qui crée
     * @return UserDTO du nouvel utilisateur créé
     * @throws Exception Si validation échoue ou permissions insuffisantes
     */
    public UserDTO createUser(CreateUserRequest request, String creatorRole) throws Exception {

        // 1️⃣ Vérifier que l'email est unique
        User existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser != null) {
            throw new Exception("Cet email est déjà utilisé");
        }

        // 2️⃣ Vérifier que le rôle à créer est valide
        RoleEnum targetRole;
        try {
            targetRole = RoleEnum.valueOf(request.getRoleCode());
        } catch (IllegalArgumentException e) {
            throw new Exception("Rôle invalide: " + request.getRoleCode());
        }

        // 3️⃣ Vérifier les permissions du créateur
        checkPermissionToCreateRole(creatorRole, targetRole);

        // 4️⃣ Récupérer le rôle depuis la BD
        Role role = roleRepository.findByCode(targetRole);
        if (role == null) {
            throw new Exception("Rôle non trouvé en base de données");
        }

        // 5️⃣ Créer l'utilisateur
        User newUser = User.builder()
                .username(request.getEmail().split("@")[0])  // Username = partie avant @
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // Hasher le password
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(role)
                .isActive(true)  // Créé par admin = activé directement
                .registrationStatus("APPROVED")  // Créé par admin = approuvé
                .build();

        // 6️⃣ Sauvegarder
        User savedUser = userRepository.save(newUser);

        // 7️⃣ Retourner le DTO
        return convertToDTO(savedUser);
    }

    /**
     * GET ALL USERS - Récupérer tous les utilisateurs
     */
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET USER BY ID - Récupérer un utilisateur spécifique
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        return convertToDTO(user);
    }

    /**
     * DELETE USER - Supprimer un utilisateur
     *
     * ⚠️ SUPER_ADMIN ONLY!
     * ⚠️ Impossible de supprimer le SUPER_ADMIN!
     *
     * @param id ID de l'utilisateur à supprimer
     * @param deleterRole Rôle de la personne qui supprime
     * @throws Exception Si permissions insuffisantes
     */
    public void deleteUser(Long id, String deleterRole) throws Exception {

        // Seul SUPER_ADMIN peut supprimer
        if (!deleterRole.equals("SUPER_ADMIN")) {
            throw new Exception("Seul SUPER_ADMIN peut supprimer des utilisateurs");
        }

        // Récupérer l'utilisateur à supprimer
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new Exception("Utilisateur non trouvé");
        }

        // Ne pas supprimer le SUPER_ADMIN
        if (user.getRole().getCode() == RoleEnum.SUPER_ADMIN) {
            throw new Exception("Impossible de supprimer le SUPER_ADMIN");
        }

        // Supprimer
        userRepository.delete(user);
    }

    /**
     * Vérifier les permissions de création d'un rôle
     *
     * PERMISSIONS:
     * ✅ SUPER_ADMIN: peut créer tous les rôles
     * ✅ REGISTRAR: peut créer STUDENT, TEACHER
     * ❌ TEACHER/STUDENT: ne peuvent créer personne
     *
     * @param creatorRole Rôle du créateur
     * @param targetRole Rôle à créer
     * @throws Exception Si permissions insuffisantes
     */
    private void checkPermissionToCreateRole(String creatorRole, RoleEnum targetRole) throws Exception {

        switch (creatorRole) {
            case "SUPER_ADMIN":
                // SUPER_ADMIN peut créer tous les rôles
                return;

            case "REGISTRAR":
                // REGISTRAR peut créer STUDENT et TEACHER
                if (targetRole == RoleEnum.STUDENT || targetRole == RoleEnum.TEACHER) {
                    return;
                }
                throw new Exception("Agent de Scolarité peut créer: STUDENT, TEACHER");

            case "TEACHER":
                throw new Exception("Enseignant ne peut créer personne");

            case "STUDENT":
                throw new Exception("Étudiant ne peut créer personne");

            default:
                throw new Exception("Rôle de créateur invalide: " + creatorRole);
        }
    }

    /**
     * Convertir User en UserDTO (sans password pour sécurité)
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().getCode().toString())
                .isActive(user.getIsActive())
                .build();
    }
}