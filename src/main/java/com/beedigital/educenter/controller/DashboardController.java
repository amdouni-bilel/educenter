package com.beedigital.educenter.controller;

import com.beedigital.educenter.dto.ApiResponse;
import com.beedigital.educenter.service.DashboardService;
import com.beedigital.educenter.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * DashboardController - Endpoints pour les dashboards
 *
 * Endpoints:
 * - GET /api/dashboard/admin              - Dashboard admin
 * - GET /api/dashboard/teacher            - Dashboard enseignant
 * - GET /api/dashboard/student            - Dashboard étudiant
 * - GET /api/dashboard/parent/{childId}   - Dashboard parent
 *
 * @author Équipe Développement
 * @version 1.0
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Dashboard ADMIN
     *
     * Voir:
     * - Nombre total d'utilisateurs
     * - Nombre d'étudiants
     * - Nombre d'enseignants
     * - Inscriptions en attente
     */
    @GetMapping("/admin")
    public ResponseEntity<?> getAdminDashboard(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String role = jwtUtil.extractRole(token);

            if (!role.equals("SUPER_ADMIN")) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "❌ Accès refusé (SUPER_ADMIN requis)", null));
            }

            Map<String, Object> dashboard = dashboardService.getAdminDashboard();

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Dashboard Admin",
                    dashboard
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Dashboard TEACHER
     *
     * Voir:
     * - Ses classes
     * - Ses étudiants
     * - Notes à valider
     * - Emploi du temps du jour
     */
    @GetMapping("/teacher")
    public ResponseEntity<?> getTeacherDashboard(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            if (!role.equals("TEACHER")) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "❌ Accès refusé (TEACHER requis)", null));
            }

            Map<String, Object> dashboard = dashboardService.getTeacherDashboard(userId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Dashboard Enseignant",
                    dashboard
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Dashboard STUDENT
     *
     * Voir:
     * - Ses notes
     * - Sa moyenne
     * - Ses absences
     * - Son emploi du temps
     */
    @GetMapping("/student")
    public ResponseEntity<?> getStudentDashboard(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            if (!role.equals("STUDENT")) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "❌ Accès refusé (STUDENT requis)", null));
            }

            Map<String, Object> dashboard = dashboardService.getStudentDashboard(userId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Dashboard Étudiant",
                    dashboard
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }

    /**
     * Dashboard PARENT
     *
     * Voir les infos de son enfant:
     * - Notes
     * - Moyenne
     * - Absences
     * - Emploi du temps
     */
    @GetMapping("/parent/{childId}")
    public ResponseEntity<?> getParentDashboard(
            @PathVariable Long childId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long parentId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            if (!role.equals("PARENT")) {
                return ResponseEntity.status(403)
                        .body(new ApiResponse(false, "❌ Accès refusé (PARENT requis)", null));
            }

            Map<String, Object> dashboard = dashboardService.getParentDashboard(parentId, childId);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "✅ Dashboard Parent",
                    dashboard
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse(false, "❌ " + e.getMessage(), null));
        }
    }
}