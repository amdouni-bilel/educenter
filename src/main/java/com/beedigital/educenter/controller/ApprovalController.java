package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.dto.RejectRequest;
import com.beedigital.educenter.service.RegistrationService;
import com.beedigital.educenter.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ApprovalController - Endpoints pour approver/rejeter les inscriptions
 *
 * ⚠️ SUPER_ADMIN ET REGISTRAR ONLY!
 *
 * @author Équipe Développement
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApprovalController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * APPROVE REGISTRATION - Approuver une inscription
     */
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveRegistration(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("✅ APPROBATION - User ID: " + id);
            System.out.println("═══════════════════════════════════════════════════════");

            String token = authHeader.replace("Bearer ", "").trim();
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "❌ Token invalide ou expiré", null));
            }

            System.out.println("✅ Token valide");

            String role = jwtUtil.extractRole(token);
            if (!role.equals("SUPER_ADMIN") && !role.equals("REGISTRAR")) {
                System.out.println("❌ Rôle insuffisant: " + role);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "❌ Seul SUPER_ADMIN et REGISTRAR peuvent approuver", null));
            }

            System.out.println("✅ Rôle valide: " + role);

            registrationService.approveRegistration(id);

            System.out.println("✅ Utilisateur approuvé");
            System.out.println("═══════════════════════════════════════════════════════");

            return ResponseEntity.ok(new ApiResponse(true,
                    "✅ Inscription approuvée! L'utilisateur peut maintenant se connecter.",
                    null));

        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
            System.out.println("═══════════════════════════════════════════════════════");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * REJECT REGISTRATION - Rejeter une inscription
     */
    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectRegistration(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) RejectRequest request) {
        try {
            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("❌ REJET - User ID: " + id);
            System.out.println("═══════════════════════════════════════════════════════");

            String token = authHeader.replace("Bearer ", "").trim();
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "❌ Token invalide ou expiré", null));
            }

            System.out.println("✅ Token valide");

            String role = jwtUtil.extractRole(token);
            if (!role.equals("SUPER_ADMIN") && !role.equals("REGISTRAR")) {
                System.out.println("❌ Rôle insuffisant: " + role);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, "❌ Seul SUPER_ADMIN et REGISTRAR peuvent rejeter", null));
            }

            System.out.println("✅ Rôle valide: " + role);

            String reason = (request != null && request.getReason() != null)
                    ? request.getReason()
                    : "Non spécifié";

            registrationService.rejectRegistration(id, reason);

            System.out.println("✅ Utilisateur rejeté");
            System.out.println("   Raison: " + reason);
            System.out.println("═══════════════════════════════════════════════════════");

            return ResponseEntity.ok(new ApiResponse(true, "✅ Inscription rejetée", null));

        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
            System.out.println("═══════════════════════════════════════════════════════");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }
}