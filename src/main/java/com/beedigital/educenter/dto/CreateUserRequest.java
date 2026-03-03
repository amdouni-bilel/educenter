package com.beedigital.educenter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreateUserRequest - DTO pour créer un utilisateur par admin
 *
 * Différence avec RegisterRequest:
 * - RegisterRequest: Self-service (STUDENT/TEACHER s'inscrivent)
 * - CreateUserRequest: Admin crée (SUPER_ADMIN/REGISTRAR créent)
 *
 * PERMISSIONS:
 * ✅ SUPER_ADMIN: Peut créer SUPER_ADMIN, REGISTRAR, TEACHER, STUDENT
 * ✅ REGISTRAR: Peut créer TEACHER, STUDENT
 * ❌ TEACHER/STUDENT: Ne peuvent créer personne
 *
 * REQUEST:
 * POST /api/users
 * Header: Authorization: Bearer {admin_token}
 * Content-Type: application/json
 *
 * {
 *   "email": "student@educenter.tn",
 *   "password": "Student@123456",
 *   "firstName": "Layla",
 *   "lastName": "Kasraoui",
 *   "phone": "+216 50 123 456",
 *   "address": "Tunis",
 *   "roleCode": "STUDENT"
 * }
 *
 * RESPONSE (201 CREATED):
 * {
 *   "success": true,
 *   "message": "✅ Utilisateur créé",
 *   "data": {
 *     "id": 2,
 *     "email": "student@educenter.tn",
 *     "firstName": "Layla",
 *     "lastName": "Kasraoui",
 *     "role": "STUDENT",
 *     "isActive": true
 *   }
 * }
 *
 * @author Équipe Développement
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    /**
     * Email de l'utilisateur
     *
     * @validation @Email - Format email valide
     * @validation @NotBlank - Ne doit pas être vide
     *
     * Caractéristiques:
     * - Doit être unique dans le système
     * - Format: user@educenter.tn
     * - Utilisé comme identifiant principal
     *
     * Exemple: student@educenter.tn
     */
    @Email(message = "❌ Email invalide. Format: user@educenter.tn")
    @NotBlank(message = "❌ Email requis")
    private String email;

    /**
     * Mot de passe en clair
     *
     * @validation @NotBlank - Ne doit pas être vide
     * @validation @Pattern - Doit être fort
     *
     * RÈGLES DE FORCE:
     * ✅ Minimum 8 caractères
     * ✅ 1 majuscule (A-Z)
     * ✅ 1 minuscule (a-z)
     * ✅ 1 chiffre (0-9)
     * ✅ 1 symbole (@$!%*?&)
     *
     * Exemples valides:
     * - Student@123456
     * - Prof#2024abc
     * - MyPass$word123
     *
     * Exemples invalides:
     * - password (pas de majuscule, chiffre, symbole)
     * - Pass123 (pas de symbole)
     * - PASS@123 (pas de minuscule)
     * - Pass@abc (pas de chiffre)
     *
     * ⚠️ IMPORTANT:
     * - Jamais stocké en clair
     * - Hashé en BCrypt avant sauvegarde
     * - Jamais retourné dans les réponses
     * - Toujours transmis via HTTPS
     */
    @NotBlank(message = "❌ Mot de passe requis")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "❌ Mot de passe: minimum 8 caractères, 1 majuscule, 1 minuscule, 1 chiffre, 1 symbole (@$!%*?&)"
    )
    private String password;

    /**
     * Prénom de l'utilisateur
     *
     * @validation @NotBlank - Ne doit pas être vide
     *
     * Exemple: Layla, Ahmed, Mariem, etc
     */
    @NotBlank(message = "❌ Prénom requis")
    private String firstName;

    /**
     * Nom de famille de l'utilisateur
     *
     * @validation @NotBlank - Ne doit pas être vide
     *
     * Exemple: Kasraoui, Khalil, Ben Ahmed, etc
     */
    @NotBlank(message = "❌ Nom requis")
    private String lastName;

    /**
     * Numéro de téléphone (optionnel)
     *
     * Caractéristiques:
     * - Facultatif
     * - Format international recommandé
     * - Exemple: +216 50 123 456
     */
    private String phone;

    /**
     * Adresse physique (optionnel)
     *
     * Caractéristiques:
     * - Facultatif
     * - Utilisé pour les documents
     * - Exemple: Tunis, Ariana, Sfax, etc
     */
    private String address;

    /**
     * Code du rôle à créer
     *
     * @validation @NotBlank - Ne doit pas être vide
     *
     * Valeurs acceptées:
     * - "SUPER_ADMIN" (si créateur = SUPER_ADMIN seulement)
     * - "REGISTRAR" (si créateur = SUPER_ADMIN seulement)
     * - "TEACHER" (SUPER_ADMIN ou REGISTRAR)
     * - "STUDENT" (SUPER_ADMIN ou REGISTRAR)
     *
     * Restrictions de création:
     * ✅ SUPER_ADMIN: peut créer TOUS les rôles
     * ✅ REGISTRAR: peut créer STUDENT, TEACHER
     * ❌ TEACHER: ne peut créer personne
     * ❌ STUDENT: ne peut créer personne
     *
     * Exemple: "STUDENT"
     */
    @NotBlank(message = "❌ Rôle requis")
    private String roleCode;
}
