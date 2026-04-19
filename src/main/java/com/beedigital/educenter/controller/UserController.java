package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.UserDTO;
import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.dto.CreateUserRequest;
import com.beedigital.educenter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ─── Créer un utilisateur ─────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody CreateUserRequest request,
            Authentication authentication) {
        try {
            String creatorRole = authentication.getAuthorities().iterator().next().getAuthority();
            UserDTO created = userService.createUser(request, creatorRole);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "✅ Utilisateur créé", created));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Tous les utilisateurs ────────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "OK", userService.getAllUsers()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Mon profil ───────────────────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "OK", userService.getUserByEmail(authentication.getName())));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Par ID ───────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "OK", userService.getUserById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Modifier ─────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest request) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "✅ Mis à jour", userService.updateUser(id, request)));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Activer / Désactiver ─────────────────────────────────────────────────
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        try {
            return ResponseEntity.ok(new ApiResponse(true, isActive ? "✅ Activé" : "⏸ Désactivé", userService.toggleStatus(id, isActive)));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Supprimer ────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse(true, "✅ Supprimé", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Demandes en attente ──────────────────────────────────────────────────
    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> getPending() {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "OK", userService.getPendingUsers()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Approuver ────────────────────────────────────────────────────────────
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        try {
            UserDTO approved = userService.approveRegistration(id);
            return ResponseEntity.ok(new ApiResponse(true, "✅ Inscription approuvée pour " + approved.getFirstName(), approved));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Rejeter ──────────────────────────────────────────────────────────────
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','REGISTRAR')")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        try {
            userService.rejectRegistration(id);
            return ResponseEntity.ok(new ApiResponse(true, "🗑️ Demande rejetée", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}