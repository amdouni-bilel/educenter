package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.LoginRequest;
import com.beedigital.educenter.dto.AuthResponse;
import com.beedigital.educenter.dto.RefreshTokenRequest;
import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.dto.VerifyResponse;
import com.beedigital.educenter.entity.User;
import com.beedigital.educenter.service.AuthService;
import com.beedigital.educenter.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * AuthController - Endpoints d'authentification
 *
 * ENDPOINTS:
 * 1. POST   /api/auth/login              → Se connecter (obtenir tokens)
 * 2. POST   /api/auth/refresh            → Rafraîchir le token d'accès
 * 3. POST   /api/auth/logout             → Se déconnecter
 * 4. GET    /api/auth/me                 → Obtenir les infos utilisateur connecté
 * 5. GET    /api/auth/verify             → Vérifier si le token est valide
 *
 * @author Équipe Développement
 * @version 2.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * LOGIN - Se connecter et obtenir les tokens
     */
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

    /**
     * REFRESH TOKEN - Rafraîchir l'access token
     */
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

    /**
     * USER INFO - Obtenir les infos de l'utilisateur connecté
     */
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

    /**
     * LOGOUT - Se déconnecter
     */
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

    /**
     * VERIFY TOKEN - Vérifier si le token est valide
     */
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