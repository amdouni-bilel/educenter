package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.*;
import com.beedigital.educenter.entity.Student;
import com.beedigital.educenter.entity.User;
import com.beedigital.educenter.enums.RoleEnum;
import com.beedigital.educenter.repositories.RoleRepository;
import com.beedigital.educenter.repositories.UserRepository;
import com.beedigital.educenter.service.AuthService;
import com.beedigital.educenter.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * AuthController - Endpoints d'authentification
 *
 * ENDPOINTS:
 * 1. POST   /api/auth/login              → Se connecter (obtenir tokens)
 * 2. POST   /api/auth/register           → Inscription étudiant (PENDING)
 * 3. POST   /api/auth/refresh            → Rafraîchir le token d'accès
 * 4. POST   /api/auth/logout             → Se déconnecter
 * 5. GET    /api/auth/me                 → Obtenir les infos utilisateur connecté
 * 6. GET    /api/auth/verify             → Vérifier si le token est valide
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── LOGIN ────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "❌ " + e.getMessage(), null, null, null, 0L));
        }
    }

    // ─── REGISTER (inscription étudiant — accès public) ──────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 1. Email déjà utilisé ?
            if (userRepository.findByEmail(request.getEmail()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse(false, "❌ Cet email est déjà utilisé", null));
            }

            // 2. Récupérer le rôle STUDENT
            var role = roleRepository.findByCode(RoleEnum.STUDENT);
            if (role == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse(false, "❌ Rôle STUDENT introuvable en base", null));
            }

            // 3. Créer l'étudiant (isActive=false, status=PENDING)
            Student student = new Student();
            student.setEmail(request.getEmail());
            student.setUsername(request.getEmail().split("@")[0]);
            student.setPassword(passwordEncoder.encode(request.getPassword()));
            student.setFirstName(request.getFirstName());
            student.setLastName(request.getLastName());
            student.setPhone(request.getPhone());
            student.setAddress(request.getAddress());
            student.setBirthDate(request.getBirthDate());
            student.setCin(request.getCin());
            student.setRole(role);
            student.setIsActive(false);
            student.setRegistrationStatus("PENDING");

            userRepository.save(student);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true,
                    "✅ Demande reçue ! Vous serez contacté par email après approbation de votre dossier.",
                    null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    // ─── REFRESH TOKEN ────────────────────────────────────────────────────────
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "❌ " + e.getMessage(), null, null, null, 0L));
        }
    }

    // ─── USER INFO ────────────────────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "❌ Token invalide ou expiré", null));
            }

            User user = authService.verifyToken(token);
            return ResponseEntity.ok(new ApiResponse(true, "✅ Infos utilisateur", user));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    // ─── LOGOUT ───────────────────────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            authService.logout(token);
            return ResponseEntity.ok(new ApiResponse(true, "✅ Déconnexion réussie", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "❌ Erreur: " + e.getMessage(), null));
        }
    }

    // ─── VERIFY TOKEN ─────────────────────────────────────────────────────────
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            boolean isValid = jwtUtil.validateToken(token);
            return ResponseEntity.ok(new VerifyResponse(isValid));
        } catch (Exception e) {
            return ResponseEntity.ok(new VerifyResponse(false));
        }
    }
}