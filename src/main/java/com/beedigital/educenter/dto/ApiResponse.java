package com.beedigital.educenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiResponse - DTO standardisé pour les réponses API
 *
 * Utilisé par TOUS les controllers pour les réponses
 *
 * FORMAT:
 * {
 *   "success": true/false,
 *   "message": "Message de réponse",
 *   "data": {...} ou [...] ou null
 * }
 *
 * EXEMPLES:
 *
 * Succès:
 * {
 *   "success": true,
 *   "message": "✅ Utilisateur créé",
 *   "data": {
 *     "id": 1,
 *     "email": "user@educenter.tn",
 *     "role": "STUDENT"
 *   }
 * }
 *
 * Erreur:
 * {
 *   "success": false,
 *   "message": "❌ Email déjà utilisé",
 *   "data": null
 * }
 *
 * @author Équipe Développement
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {

    /**
     * Indicateur de succès
     * - true: Opération réussie
     * - false: Opération échouée
     */
    private Boolean success;

    /**
     * Message de réponse
     * - Succès: "✅ Description"
     * - Erreur: "❌ Description"
     */
    private String message;

    /**
     * Données de réponse
     * - Objet: UserDTO, RoleDTO, etc
     * - Liste: List<UserDTO>
     * - null: Pas de données
     */
    private Object data;
}
