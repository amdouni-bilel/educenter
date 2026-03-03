package com.beedigital.educenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RejectRequest - DTO pour rejeter une inscription
 *
 * Utilisé par: ApprovalController.rejectRegistration()
 *
 * REQUEST:
 * PUT /api/users/reject/2
 * Header: Authorization: Bearer {admin_token}
 * Content-Type: application/json
 *
 * {
 *   "reason": "Diplôme non valide"
 * }
 *
 * OU sans raison:
 *
 * {
 *   "reason": ""
 * }
 *
 * RESPONSE (200 OK):
 * {
 *   "success": true,
 *   "message": "✅ Inscription rejetée",
 *   "data": null
 * }
 *
 * RAISONS COMMUNES:
 * - "Diplôme non valide"
 * - "Documents incomplets"
 * - "Email non valide"
 * - "Informations suspectes"
 * - "Doublon détecté"
 * - "Non spécifié"
 *
 * @author Équipe Développement
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectRequest {

    /**
     * Raison du rejet (optionnel)
     *
     * Utilisée pour:
     * - Informer l'utilisateur pourquoi son inscription est rejetée
     * - Archiver les raisons des rejets (audit)
     * - Email de notification
     *
     * Si vide, la BD stocke "Non spécifié"
     */
    private String reason;
}
