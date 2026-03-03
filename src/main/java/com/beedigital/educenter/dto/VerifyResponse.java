package com.beedigital.educenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VerifyResponse - DTO pour vérifier si un token est valide
 *
 * Utilisé par: AuthController.verifyToken()
 *
 * REQUEST:
 * GET /api/auth/verify
 * Header: Authorization: Bearer {token}
 *
 * RESPONSE (200 OK):
 * {
 *   "valid": true
 * }
 *
 * OU
 *
 * {
 *   "valid": false
 * }
 *
 * USAGE:
 * - Frontend: Vérifier avant de faire une requête
 * - Frontend: Refresh le token si invalide
 * - Tests: Valider que le token fonctionne
 *
 * @author Équipe Développement
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyResponse {

    /**
     * Indicateur de validité du token
     *
     * Valeurs:
     * - true: Token valide et non expiré
     * - false: Token invalide, expiré, ou corrompu
     */
    private Boolean valid;
}
