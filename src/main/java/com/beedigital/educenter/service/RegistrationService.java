package com.beedigital.educenter.service;

import com.beedigital.educenter.dto.RegisterRequest;
import com.beedigital.educenter.dto.UserDTO;
import com.beedigital.educenter.entity.User;
import com.beedigital.educenter.entity.Role;
import com.beedigital.educenter.enums.RoleEnum;
import com.beedigital.educenter.repositories.UserRepository;
import com.beedigital.educenter.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RegistrationService - Service pour l'inscription self-service
 *
 * WORKFLOW:
 * 1. STUDENT/TEACHER s'inscrit via /api/auth/register
 * 2. Compte créé avec status: PENDING (inactif)
 * 3. SUPER_ADMIN/REGISTRAR approuve dans le dashboard
 * 4. Si approuvé: compte devient ACTIVE
 * 5. STUDENT/TEACHER peut se connecter
 *
 * RESTRICTIONS:
 * ✅ Peuvent s'inscrire: STUDENT, TEACHER
 * ❌ Impossible: SUPER_ADMIN, REGISTRAR (créés par admin seulement)
 *
 * @author Équipe Développement
 * @version 1.0
 */
@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * REGISTER USER - Permettre aux STUDENT/TEACHER de s'inscrire
     *
     * Validations:
     * 1. Email unique?
     * 2. Password fort?
     * 3. Rôle valide (STUDENT ou TEACHER)?
     * 4. Rôle existe en BD?
     *
     * @param request Données d'inscription
     * @return UserDTO du nouvel utilisateur créé (status = PENDING)
     * @throws Exception Si validation échoue
     */
    public UserDTO registerUser(RegisterRequest request) throws Exception {

        // 1️⃣ Vérifier que l'email est unique
        User existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser != null) {
            throw new Exception("Cet email est déjà utilisé");
        }

        // 2️⃣ Vérifier que le rôle à créer est STUDENT ou TEACHER
        RoleEnum targetRole;
        try {
            targetRole = RoleEnum.valueOf(request.getRoleCode());
        } catch (IllegalArgumentException e) {
            throw new Exception("Rôle invalide: " + request.getRoleCode());
        }

        // 3️⃣ Vérifier que seuls STUDENT et TEACHER peuvent s'inscrire
        if (targetRole != RoleEnum.STUDENT && targetRole != RoleEnum.TEACHER) {
            throw new Exception("Seul STUDENT et TEACHER peuvent s'inscrire");
        }

        // 4️⃣ Récupérer le rôle depuis la BD
        Role role = roleRepository.findByCode(targetRole);
        if (role == null) {
            throw new Exception("Rôle non trouvé en base de données");
        }

        // 5️⃣ Créer l'utilisateur avec status PENDING
        User newUser = User.builder()
                .username(request.getEmail().split("@")[0])  // Username = partie avant @
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // Hasher le password
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(role)
                .isActive(false)  // INACTIF jusqu'à approbation
                .registrationStatus("PENDING")  // En attente d'approbation
                .createdAt(LocalDateTime.now())
                .build();

        // 6️⃣ Sauvegarder
        User savedUser = userRepository.save(newUser);

        System.out.println("✅ Nouvel utilisateur créé (PENDING): " + savedUser.getEmail());

        // 7️⃣ Retourner le DTO
        return convertToDTO(savedUser);
    }

    /**
     * GET PENDING REGISTRATIONS - Lister les inscriptions en attente
     *
     * ⚠️ SUPER_ADMIN ET REGISTRAR ONLY!
     *
     * @return Liste des utilisateurs en attente d'approbation
     */
    public List<UserDTO> getPendingRegistrations() {
        // Récupérer tous les utilisateurs avec status = PENDING
        List<User> pendingUsers = userRepository.findAll().stream()
                .filter(user -> "PENDING".equals(user.getRegistrationStatus()))
                .collect(Collectors.toList());

        return pendingUsers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * APPROVE REGISTRATION - Approuver une inscription
     *
     * ⚠️ SUPER_ADMIN ET REGISTRAR ONLY!
     *
     * Actions:
     * - isActive = TRUE
     * - registrationStatus = APPROVED
     * - approvedAt = maintenant
     *
     * @param userId ID de l'utilisateur à approuver
     * @throws Exception Si utilisateur non trouvé ou pas PENDING
     */
    public void approveRegistration(Long userId) throws Exception {

        // Chercher l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé"));

        // Vérifier que le statut est PENDING
        if (!"PENDING".equals(user.getRegistrationStatus())) {
            throw new Exception("Cette inscription n'est pas en attente");
        }

        // Mettre à jour
        user.setIsActive(true);
        user.setRegistrationStatus("APPROVED");
        user.setApprovedAt(LocalDateTime.now());

        // Sauvegarder
        userRepository.save(user);

        System.out.println("✅ Inscription approuvée: " + user.getEmail());
    }

    /**
     * REJECT REGISTRATION - Rejeter une inscription
     *
     * ⚠️ SUPER_ADMIN ET REGISTRAR ONLY!
     *
     * Actions:
     * - registrationStatus = REJECTED
     * - rejectionReason = raison
     * - rejectedAt = maintenant
     *
     * @param userId ID de l'utilisateur à rejeter
     * @param reason Raison du rejet
     * @throws Exception Si utilisateur non trouvé ou pas PENDING
     */
    public void rejectRegistration(Long userId, String reason) throws Exception {

        // Chercher l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé"));

        // Vérifier que le statut est PENDING
        if (!"PENDING".equals(user.getRegistrationStatus())) {
            throw new Exception("Cette inscription n'est pas en attente");
        }

        // Mettre à jour
        user.setRegistrationStatus("REJECTED");
        user.setRejectionReason(reason != null && !reason.isEmpty() ? reason : "Non spécifié");
        user.setRejectedAt(LocalDateTime.now());
        // isActive reste FALSE

        // Sauvegarder
        userRepository.save(user);

        System.out.println("❌ Inscription rejetée: " + user.getEmail() + " (Raison: " + reason + ")");
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