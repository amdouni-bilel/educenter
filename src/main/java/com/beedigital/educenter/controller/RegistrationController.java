package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.RegisterRequest;
import com.beedigital.educenter.dto.UserDTO;
import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.service.RegistrationService;
import com.beedigital.educenter.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * RegistrationController - Endpoints pour l'inscription self-service
 *
 * @author Équipe Développement
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * REGISTER - S'inscrire (STUDENT ou TEACHER seulement)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("📝 INSCRIPTION: " + request.getEmail());
            System.out.println("═══════════════════════════════════════════════════════");

            UserDTO newUser = registrationService.registerUser(request);

            System.out.println("✅ Inscription créée (en attente d'approbation)");
            System.out.println("═══════════════════════════════════════════════════════");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true,
                            "✅ Inscription réussie! Votre compte est en attente d'approbation",
                            newUser));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * GET PENDING REGISTRATIONS - Lister les inscriptions en attente
     */
    @GetMapping("/pending-registrations")
    public ResponseEntity<?> getPendingRegistrations(
            @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("📋 Récupération des inscriptions en attente");
            System.out.println("═══════════════════════════════════════════════════════");

            String token = authHeader.replace("Bearer ", "").trim();
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "❌ Token invalide ou expiré", null));
            }

            String role = jwtUtil.extractRole(token);
            if (!role.equals("SUPER_ADMIN") && !role.equals("REGISTRAR")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "❌ Seul SUPER_ADMIN et REGISTRAR peuvent voir les inscriptions", null));
            }

            List<UserDTO> pendingUsers = registrationService.getPendingRegistrations();

            System.out.println("✅ Inscriptions trouvées: " + pendingUsers.size());
            System.out.println("═══════════════════════════════════════════════════════");

            return ResponseEntity.ok(new ApiResponse(true, "✅ Inscriptions en attente", pendingUsers));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "❌ Erreur: " + e.getMessage(), null));
        }
    }
}