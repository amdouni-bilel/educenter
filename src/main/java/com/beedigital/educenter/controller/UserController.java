package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.UserDTO;
import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.dto.CreateUserRequest;
import com.beedigital.educenter.service.UserService;
import com.beedigital.educenter.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * UserController - CRUD des utilisateurs avec gestion des permissions
 *
 * @author Équipe Développement
 * @version 2.0
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * CREATE USER - Créer un nouvel utilisateur
     */
    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateUserRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "❌ Token invalide ou expiré", null));
            }

            String creatorRole = jwtUtil.extractRole(token);
            UserDTO newUser = userService.createUser(request, creatorRole);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "✅ Utilisateur créé avec succès", newUser));

        } catch (Exception e) {
            int statusCode = e.getMessage().contains("peut créer") ? 403 : 400;
            return ResponseEntity.status(statusCode)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * GET ALL USERS - Lister tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "❌ Token invalide ou expiré", null));
            }

            List<UserDTO> users = userService.getAllUsers();

            return ResponseEntity.ok(new ApiResponse(true, "✅ Utilisateurs récupérés", users));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "❌ Erreur: " + e.getMessage(), null));
        }
    }

    /**
     * GET USER BY ID - Obtenir un utilisateur spécifique
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "❌ Token invalide ou expiré", null));
            }

            UserDTO user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "❌ Utilisateur non trouvé", null));
            }

            return ResponseEntity.ok(new ApiResponse(true, "✅ Utilisateur trouvé", user));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "❌ Erreur: " + e.getMessage(), null));
        }
    }

    /**
     * DELETE USER - Supprimer un utilisateur
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "❌ Token invalide ou expiré", null));
            }

            String role = jwtUtil.extractRole(token);
            userService.deleteUser(id, role);

            return ResponseEntity.ok(new ApiResponse(true, "✅ Utilisateur supprimé", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }
}