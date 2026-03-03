package com.beedigital.educenter.service;

import com.beedigital.educenter.dto.AuthResponse;
import com.beedigital.educenter.dto.UserDTO;
import com.beedigital.educenter.entity.User;
import com.beedigital.educenter.repositories.UserRepository;
import com.beedigital.educenter.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService - Service d'authentification avec Access Token + Refresh Token
 *
 * VERSION: 2.0 - FINAL
 *
 * FLOW AUTHENTIFICATION:
 *
 * 1️⃣ LOGIN:
 *    - User envoie email + password
 *    - Backend valide et crée 2 tokens:
 *      • accessToken (1h) - pour les requêtes API
 *      • refreshToken (7j) - pour obtenir nouveau accessToken
 *    - Frontend stocke les deux
 *
 * 2️⃣ REQUÊTE API:
 *    - Frontend envoie accessToken dans Authorization header
 *    - Backend valide le token
 *    - Si valide: exécute la requête ✅
 *    - Si expiré: retourne 401
 *
 * 3️⃣ REFRESH TOKEN (quand accessToken expire):
 *    - Frontend utilise le refreshToken pour obtenir un nouvel accessToken
 *    - Backend valide le refreshToken
 *    - Si valide: crée un nouvel accessToken ✅
 *
 * DURÉES:
 * - accessToken: 1h (3600 secondes)
 * - refreshToken: 7j (604800 secondes)
 *
 * @author Équipe Développement
 * @version 2.0
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * LOGIN - Authentifier un utilisateur et créer access + refresh tokens
     *
     * Vérifications (4 étapes):
     * 1. Email existe?
     * 2. Password correct?
     * 3. Compte actif (isActive = TRUE)?
     * 4. Registration approuvé (registrationStatus = APPROVED)?
     *
     * @param email Email de l'utilisateur
     * @param password Mot de passe en clair
     * @return AuthResponse avec accessToken + refreshToken
     * @throws Exception Si email/password incorrect ou compte inactif
     */
    public AuthResponse login(String email, String password) throws Exception {

        // 1️⃣ CHECK 1: Email existe?
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("Email ou mot de passe incorrect");
        }

        // 2️⃣ CHECK 2: Password correct?
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Email ou mot de passe incorrect");
        }

        // 3️⃣ CHECK 3: Compte actif?
        if (!user.getIsActive()) {
            throw new Exception("Compte désactivé. Contactez l'administrateur.");
        }

        // 4️⃣ CHECK 4: Registration approuvé?
        String registrationStatus = user.getRegistrationStatus();
        if (registrationStatus == null) {
            registrationStatus = "APPROVED";  // Legacy users
        }

        if (registrationStatus.equals("PENDING")) {
            throw new Exception("Votre compte est en attente d'approbation");
        }

        if (registrationStatus.equals("REJECTED")) {
            String reason = user.getRejectionReason() != null ? user.getRejectionReason() : "Non spécifié";
            throw new Exception("Votre compte a été rejeté. Raison: " + reason);
        }

        if (!registrationStatus.equals("APPROVED")) {
            throw new Exception("Statut d'inscription invalide");
        }

        // ✅ TOUS LES CHECKS PASSENT - Générer les tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Créer le DTO utilisateur (sans password)
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().getCode().toString())
                .isActive(user.getIsActive())
                .build();

        // Retourner la réponse
        return AuthResponse.builder()
                .success(true)
                .message("✅ Connexion réussie")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userDTO)
                .expiresIn(3600L)  // 1 heure en secondes
                .build();
    }

    /**
     * REFRESH TOKEN - Obtenir un nouvel accessToken avec le refreshToken
     *
     * Quand l'accessToken expire (après 1h), utiliser le refreshToken
     * pour obtenir un nouvel accessToken sans se reconnecter.
     *
     * @param refreshToken Le refreshToken
     * @return AuthResponse avec nouvel accessToken
     * @throws Exception Si refreshToken invalide ou expiré
     */
    public AuthResponse refreshToken(String refreshToken) throws Exception {

        // 1. Valider le refreshToken
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new Exception("Refresh token invalide ou expiré");
        }

        // 2. Extraire l'email du refreshToken
        String email = jwtUtil.extractEmail(refreshToken);

        // 3. Chercher l'utilisateur
        User user = userRepository.findByEmail(email);
        if (user == null || !user.getIsActive()) {
            throw new Exception("Utilisateur non trouvé ou compte désactivé");
        }

        // 4. Générer un nouvel accessToken
        String newAccessToken = jwtUtil.generateAccessToken(user);

        // 5. Retourner le nouvel accessToken
        return AuthResponse.builder()
                .success(true)
                .message("✅ Token rafraîchi")
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600L)
                .build();
    }

    /**
     * LOGOUT - Déconnecter un utilisateur
     *
     * Actuellement côté frontend (supprimer les tokens)
     * On peut aussi blacklister le token côté backend avec Redis
     *
     * @param token JWT token
     */
    public void logout(String token) {
        System.out.println("✅ Utilisateur déconnecté");
        // TODO: Implémenter le blacklist des tokens (Redis)
    }

    /**
     * VERIFY TOKEN - Vérifier si un token est valide et retourner l'utilisateur
     *
     * @param token JWT token
     * @return L'utilisateur associé au token
     * @throws Exception Si token invalide
     */
    public User verifyToken(String token) throws Exception {
        // Récupérer l'email du token
        String email = jwtUtil.extractEmail(token);

        // Chercher l'utilisateur
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("Utilisateur non trouvé");
        }

        return user;
    }
}