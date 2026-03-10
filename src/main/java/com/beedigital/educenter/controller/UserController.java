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

    // ─── Créer un utilisateur ──────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody CreateUserRequest request,
            Authentication authentication) {
        try {
            String creatorRole = authentication.getAuthorities().iterator().next().getAuthority();
            UserDTO newUser = userService.createUser(request, creatorRole);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Utilisateur créé avec succès", newUser));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Lister tous les utilisateurs ─────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(new ApiResponse(true, "Utilisateurs récupérés", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Mon propre profil (tous les rôles) ────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            UserDTO user = userService.getUserByEmail(email);
            return ResponseEntity.ok(new ApiResponse(true, "Profil récupéré", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Détail d'un utilisateur par ID ───────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Utilisateur trouvé", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Utilisateur non trouvé", null));
        }
    }

    // ─── Modifier un utilisateur ───────────────────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody CreateUserRequest request) {
        try {
            UserDTO updated = userService.updateUser(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Utilisateur mis à jour", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Activer / Désactiver un utilisateur ───────────────────────────────────
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> toggleUserStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {
        try {
            UserDTO updated = userService.toggleStatus(id, isActive);
            String msg = isActive ? "Utilisateur activé" : "Utilisateur désactivé";
            return ResponseEntity.ok(new ApiResponse(true, msg, updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ─── Supprimer un utilisateur ──────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse(true, "Utilisateur supprimé", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}